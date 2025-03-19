package com.example.attendease.color

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

const val VISIBLE_ITEMS_MESSAGE = "Visible items must be positive"

@Composable
fun ColorList(
    visibleItems: Int,
    modifier: Modifier = Modifier,
    state: CustomListState = rememberCustomListState(),
    onItemClick: (Int, Offset) -> Unit,
    content: @Composable () -> Unit
) {
    check(visibleItems > 0) { VISIBLE_ITEMS_MESSAGE }

    Layout(
        modifier = modifier
            .clipToBounds()
            .customDrag(state, onItemClick), content = content
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / visibleItems
        val itemConstraints = Constraints.fixed(width = itemWidth, height = constraints.maxHeight)
        val placeables = measurables.map { measurable -> measurable.measure(itemConstraints) }

        state.setup(
            CustomListConfig(
                contentWidth = constraints.maxWidth.toFloat(),
                numItems = placeables.size,
                visibleItems = visibleItems
            )
        )

        layout(
            width = constraints.maxWidth, height = constraints.maxHeight
        ) {
            for (i in state.firstVisibleItem..state.lastVisibleItem) {
                placeables[i].placeRelativeWithLayer(state.offsetFor(i), layerBlock = {
                    scaleX = state.scale(i)
                    scaleY = state.scale(i)
                })
            }
        }
    }
}

data class CustomListConfig(
    val contentWidth: Float = 0f, val numItems: Int = 0, val visibleItems: Int = 0
)

@Stable
interface CustomListState {
    val horizontalOffset: Float
    val firstVisibleItem: Int
    val lastVisibleItem: Int

    suspend fun snapToCenter(value: Float)
    fun calculateItemIndex(clickPositionX: Float): Int?
    suspend fun snapTo(value: Float)
    suspend fun decayTo(velocity: Float, value: Float)
    suspend fun stop()
    fun offsetFor(index: Int): IntOffset
    fun setup(config: CustomListConfig)
    fun alpha(i: Int): Float
    fun scale(i: Int): Float
}

class CustomListStateImpl(
    currentOffset: Float = 0f
) : CustomListState {

    private val animatable = Animatable(currentOffset)
    private var itemWidth = 0f
    private var config = CustomListConfig()
    private var initialOffset = 0f

    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    private val minOffset: Float
        get() = -(config.numItems - 1) * itemWidth

    override val horizontalOffset: Float
        get() = animatable.value

    override val firstVisibleItem: Int
        get() = ((-horizontalOffset - initialOffset) / itemWidth).toInt().coerceAtLeast(0)

    override val lastVisibleItem: Int
        get() = (((-horizontalOffset - initialOffset) / itemWidth).toInt() + config.visibleItems).coerceAtMost(
            config.numItems - 1
        )

    override suspend fun snapTo(value: Float) {
        animatable.snapTo(value.coerceIn(-(config.numItems - 1) * itemWidth, 0f))
    }

    override suspend fun snapToCenter(value: Float) {
        val clickedItemIndex = calculateItemIndex(value)
        clickedItemIndex?.let {
            val centerOffset = -clickedItemIndex * itemWidth
            decayTo(0f, centerOffset)
        }
        println("$clickedItemIndex")
    }

    override fun calculateItemIndex(clickPositionX: Float): Int? {
        val itemWidth = config.contentWidth / config.visibleItems
        val offsetX = clickPositionX - horizontalOffset - initialOffset
        val index = (offsetX / itemWidth).toInt()
        return if (index in 0 until config.numItems) index else null
    }

    override suspend fun decayTo(velocity: Float, value: Float) {
        val constrainedValue = value.coerceIn(minOffset, 0f).absoluteValue
        val remainder = (constrainedValue / itemWidth) - (constrainedValue / itemWidth).toInt()
        val extra = if (remainder <= 0.5f) 0 else 1
        val target = ((constrainedValue / itemWidth).toInt() + extra) * itemWidth
        animatable.animateTo(
            targetValue = -target,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override suspend fun stop() {
        animatable.stop()
    }

    override fun setup(config: CustomListConfig) {
        this.config = config
        itemWidth = config.contentWidth / config.visibleItems
        initialOffset = (config.contentWidth - itemWidth) / 2f
    }

    override fun offsetFor(index: Int): IntOffset {
        val maxOffset = config.contentWidth / 2f + itemWidth / 2f
        val x = (horizontalOffset + initialOffset + index * itemWidth)

        val deltaFromCenter = (x - initialOffset)
        val radius = config.contentWidth
        deltaFromCenter.absoluteValue * (radius / maxOffset)
        val yOffset = lerp((150).dp, (-150).dp, x / maxOffset).coerceIn((-150).dp, (150).dp)

        return IntOffset(
            x = x.roundToInt(), y = yOffset.value.roundToInt()
        )
    }

    override fun alpha(i: Int): Float {
        val maxOffset = config.contentWidth / 2f
        val x = (horizontalOffset + initialOffset + i * itemWidth)
        val deltaFromCenter = (x - initialOffset)
        val percentFromCenter = 1.0f - abs(deltaFromCenter) / maxOffset

        return 0.25f + (percentFromCenter * 0.75f)
    }

    override fun scale(i: Int): Float {
        val maxOffset = config.contentWidth / 2f
        val x = (horizontalOffset + initialOffset + i * itemWidth)
        val deltaFromCenter = (x - initialOffset)
        1.0f - abs(deltaFromCenter) / maxOffset
        return 1f - (1f - 0.65f) * (deltaFromCenter / maxOffset).absoluteValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomListStateImpl

        if (animatable.value != other.animatable.value) return false
        if (itemWidth != other.itemWidth) return false
        if (config != other.config) return false
        if (initialOffset != other.initialOffset) return false
        if (decayAnimationSpec != other.decayAnimationSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animatable.value.hashCode()
        result = 31 * result + itemWidth.hashCode()
        result = 31 * result + config.hashCode()
        result = 31 * result + initialOffset.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }

    companion object {
        val Saver = Saver<CustomListStateImpl, List<Any>>(save = { listOf(it.horizontalOffset) },
            restore = {
                CustomListStateImpl(it[0] as Float)
            })
    }
}


@Composable
fun rememberCustomListState(): CustomListState {
    val state = rememberSaveable(saver = CustomListStateImpl.Saver) {
        CustomListStateImpl()
    }
    return state
}

@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
private fun Modifier.customDrag(
    state: CustomListState, onClick: (Int, Offset) -> Unit
) = pointerInput(Unit) {
    val decay = splineBasedDecay<Float>(this)
    coroutineScope {
        while (true) {
            val down = awaitPointerEventScope { awaitFirstDown() }
            val pointerId = down.id
            state.stop()

            val tracker = VelocityTracker()
            var isDragging = false

            awaitPointerEventScope {
                horizontalDrag(pointerId) { change ->
                    isDragging = true
                    val horizontalDragOffset = state.horizontalOffset + change.positionChange().x
                    launch {
                        state.snapTo(horizontalDragOffset)
                    }
                    tracker.addPosition(change.uptimeMillis, change.position)
                    change.consume()
                }
            }

            if (isDragging) {
                val velocity = tracker.calculateVelocity().x
                val targetValue = decay.calculateTargetValue(
                    state.horizontalOffset, velocity
                )
                launch {
                    state.decayTo(velocity, targetValue)
                }
            } else {
                val index = state.calculateItemIndex(down.position.x)
                index?.let {
                    onClick(it, down.position)
                }
                launch {
                    state.snapToCenter(down.position.x)
                }
            }
        }
    }
}