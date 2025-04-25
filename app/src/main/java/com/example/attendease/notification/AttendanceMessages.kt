package com.example.attendease.notification

fun getAttendanceMessage(currentAttendance: Int, targetAttendance: Float): String {
    return when {
        currentAttendance > targetAttendance -> {
            listOf(
                "🎉 Ayeee! Attendance = fire 🔥🔥🔥 Keep cruising, topper 😎📚",
                "😏 You’re over the line — in a good way! Keep bossin’ it 💼📈",
                "🚀 Attendance flying higher than Elon’s rockets 🌌💯",
                "🏆 You’re ahead of the game! Flex that record 📊💪",
                "🔥 You’ve earned a lil chill. But still, class > bunk 😜💼",
                "🏆 Top-tier attendance? I see you, golden child ✨",
                "😎 You’re killing it! Even attendance is blushing",
                "💖 Overachiever alert! I’d swipe right on that dedication 🔥",
                "📚 You’re not just attending, you’re owning it 💼",
                "🎓 Teach me how to be as consistent as you 😮‍💨",
                "🥵 Attendance so high, even HR would hire you on sight.",
                "👀 You’re the kind of student mom brags about. Respect.",
                "💼 You walk into class like it’s your runway. Werk it.",
                "💘 Attendance that fine should come with a warning label.",
                "✨ You’re not just showing up. You’re stealing the show."
            ).random()
        }
        currentAttendance.toFloat() == targetAttendance -> {
            listOf(
                "🎯 Precision mode: ON. You’re right on target 🎓⏱️ Stay sharp 🧠✨",
                "😌 So clean. So perfect. Keep the streak alive ✨📚",
                "💪 Nailed it! One wrong move though, and 💥💥",
                "✅ Balanced like a yogi. Stay focused, don’t slip 🧘📈",
                "🛡️ You hit the mark! Now defend it like a warrior ⚔️📊",
                "💑 You and attendance = perfect match! Keep showing up, cutie 😉",
                "🌹Every class you attend makes me fall for you a little more 💕",
                "✨ You’re the main character — and your attendance proves it 😌📚",
                "🎯 Love that you're keeping it steady. This is what loyalty looks like 💘",
                "💞 Attendance on point. Just like your vibes ✨",
                "💞 You and your attendance? Finally on good terms. I’m proud.",
                "🎯 You hit the target like you hit my weak spot. Clean. Precise.",
                "🌹 This kind of consistency is hotter than your last situationship 🔥",
                "📖 Showing up every day? Commitment looks good on you.",
                "💼 If loyalty was a subject, you'd top it. Stay that way 😏"
            ).random()
        }
        else -> {
            listOf(
                "😬 Uh-oh... You’re slipping! Pull up that attendance parachute 🪂📉",
                "⚠️ Red zone alert! Time to hustle back to class 💨📚",
                "📉 You’re down bad... but you got this 🛠️ Start showing up! 💼",
                "👀 Missing class = missing goals. Fix it fam! 🔧💡",
                "💔 Below target? Don’t ghost your classes 👻 Get back on track! 🔄",
                "👻 Keep skipping class and your degree will skip you too 😌",
                "😵‍💫 One more bunk and your attendance gonna get ick of you.",
                "😩 Your attendance is giving 'bare minimum situationship' vibes.",
                "🚩 Even your toxic ex showed up more than you do 💀",
                "📚 Class misses you. I don’t. But class does.",
                "🥺 You ghosted class again? I thought we had something special 💔📚",
                "💌 Your attendance wrote a love letter... but you never replied 😢",
                "😫 Skipping class hurts me more than it hurts you, y’know 💘",
                "🎭 Why you playing hard to get with your subjects, hmm? 😏",
                "📚 Your timetable waits for you like I wait for your texts... in vain 💔",
                "👀 I miss you more than your teacher does — and that’s saying something 🥲",
                "😶 One more bunk and I’m sending a sad playlist to your attendance 💿",
                "💘 You and class were a power couple… until you started ghosting 😞",
                "📉 Your attendance wants to DTR (define the relationship) 😬",
                "😇 If you love me, you’ll attend class today. No pressure though… 👀",
                "📉 Brooo... your attendance is playing hide & seek. Come back to class? 👀",
                "👻 You’ve skipped enough to become a myth in class. Time for a comeback?",
                "💌 Your attendance misses you more than your school crush ever did 😢",
                "😬 Bunk-o-meter says: you're approaching ‘parent-teacher meeting’ zone 💣",
                "🥲 Even the teacher forgot your face... show up and shock everyone 🔥",
                "🎒 Your bag is gathering dust, bro. Class is calling 📞",
                "💀 Attendance game = weak. Time to level up, hero 🕹️",
                "🧠 Your brain’s ready for class, but you out here ghosting 📚💔",
                "📉 You’re down bad... but every hero has a comeback arc. Start today 💪",
                "😵‍💫 One more bunk and your attendance will apply for leave too 📝",
                "🚪The door to success is in class... not your bed 😴",
                "🎯 Target missed. But guess what? You still got this!",
                "😶 Bunk now, panic later? Or show up and chill guilt-free 🧘",
                "📊 Attendance: not vibing. You: need to fix that.",
                "📚 Skipped again? Your timetable just filed a complaint 😤"
            ).random()
        }
    }
}
