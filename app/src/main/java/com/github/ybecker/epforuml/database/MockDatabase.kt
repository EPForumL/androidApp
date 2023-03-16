package com.github.ybecker.epforuml.database

import com.github.ybecker.epforuml.database.Model.*

/**
 * This class is a database that should only be used for tests
 */
class MockDatabase : Database() {

    private val questions = hashMapOf<String, Question>()
    private val answers = hashMapOf<String, Answer>()
    private val users = hashMapOf<String, User>()
    private val courses = hashMapOf<String, Course>()

    init {
        val course1 = Course("course0","Sweng", mutableListOf())
        courses[course1.courseId] = course1
        val course2 = Course("course1","SDP", mutableListOf())
        courses[course2.courseId] = course2

        val user1 = User("user1", "TestUser", emptyList(), emptyList(), emptyList())
        users[user1.userId] = user1

        val question1 = Question("question1", "course1", "user1",
                                "How do I fix the CI ?", mutableListOf())
        questions[question1.questionId] = question1
        val question2 = Question("question2", "course0", "user1",
                                "What is a Scrum Master ?", mutableListOf())
        questions[question2.questionId] = question2
    }

    override fun getCourseQuestions(course: Course): Set<Question> {
        return questions.filterValues { it.courseId == course.courseId }.values.toSet()
    }

    override fun getQuestionAnswers(question: Question): Set<Answer> {
        return answers.filterValues { it.questionId == question.questionId }.values.toSet()
    }

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        val questionId = "question${questions.size + 1}"
        val question = Question(questionId, course.courseId, user.userId, questionText ?: "", emptyList())
        questions[questionId] = question
        courses[course.courseId]?.questions = courses[course.courseId]?.questions?.plus(question) ?: listOf(question)
        users[user.userId]?.let {
            val updatedQuestions = it.questions + question
            users[user.userId] = it.copy(questions = updatedQuestions)
        }
        return question
    }

    override fun addAnswer(user: User, question: Question, answerText: String?): Answer {
        val answerId = "answer${answers.size + 1}"
        val answer = Answer(answerId, question.questionId, user.userId, answerText ?: "")
        answers[answerId] = answer
        questions[question.questionId]?.answers = questions[question.questionId]?.answers?.plus(answer) ?: listOf(answer)
        courses[question.courseId]?.questions?.let { courseQuestions ->
            courseQuestions?.forEach { courseQuestion ->
                if (courseQuestion.questionId == question.questionId) {
                    courseQuestion.answers = questions[question.questionId]?.answers?.plus(answer) ?: listOf(answer)
                }
            }
        }

        users[user.userId]?.let {
            val updatedAnswers = it.answers + answer
            users[user.userId] = it.copy(answers = updatedAnswers)
        }
        return answer
    }

    override fun addUser(userId:String, username: String): User {
        val newUserId = "user${users.size + 1}"
        val user = User(newUserId , username, emptyList(), emptyList(), emptyList())
        users[userId] = user
        return user
    }

    override fun addSubscription(user: User, course: Course): User? {
        if (users[user.userId] == null) {
            return null
        } else {
            users[user.userId]?.let {
                val subscriptions = it.subscriptions.toMutableList()
                if (!subscriptions.contains(course)) {
                    subscriptions.add(course)
                }
                val updatedSubscription = subscriptions.toList()
                users[user.userId] = it.copy(subscriptions = updatedSubscription)
            }
            return users[user.userId]
        }
    }


    override fun availableCourses(): Set<Course> {
        return courses.values.toSet()
    }

    override fun getQuestionById(id: String): Question? {
        return questions.get(id)
    }

    override fun getAnswerById(id: String): Answer? {
        return answers.get(id)
    }

    override fun getUserById(id: String): User? {
        return users.get(id)
    }

    override fun getCourseById(id: String): Course? {
        return courses.get(id)
    }

    override fun getUserQuestions(user: User): Set<Question> {
        return questions.filterValues { it.userId == user.userId }.values.toSet()
    }

    override fun getUserAnswers(user: User): Set<Answer> {
        return answers.filterValues { it.userId == user.userId }.values.toSet()
    }

    override fun getUserSubscriptions(user: User): Set<Course> {
        return user.subscriptions.toSet()
    }

}