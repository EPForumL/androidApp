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

        val course1 = Course("0","Sweng", mutableListOf())
        courses[course1.courseId] = course1
        val course2 = Course("1","SDP", mutableListOf())
        courses[course2.courseId] = course2
        val user1 = User("user1", "TestUser", emptyList(), emptyList())
        users[user1.userId] = user1
    }

    override fun getQuestionsForCourse(course: Course): List<Question> {
        return questions.filterValues { it.courseId == course.courseId }.values.toList()
    }

    override fun getAnswersForQuestion(question: Question): List<Answer> {
        return answers.filterValues { it.questionId == question.questionId }.values.toList()
    }

    override fun addQuestion(user: User, course: Course, questionText: String?): Question {
        val questionId = "question${questions.size + 1}"
        val question = Question(questionId, course.courseId, user.userId, questionText ?: "", emptyList())
        questions[questionId] = question
        courses[course.courseId]?.answers = courses[course.courseId]?.answers?.plus(question) ?: listOf(question)
        users[user.userId]?.let {
            val updatedQuestions = it.questions + question
            users[user.userId] = it.copy(questions = updatedQuestions)
        }
        return question
    }

    override fun addAnswers(user: User, question: Question, answerText: String?): Answer {
        val answerId = "answer${answers.size + 1}"
        val answer = Answer(answerId, question.questionId, user.userId, answerText ?: "")
        answers[answerId] = answer
        questions[question.questionId]?.answers = questions[question.questionId]?.answers?.plus(answer) ?: listOf(answer)
        users[user.userId]?.let {
            val updatedAnswers = it.answers + answer
            users[user.userId] = it.copy(answers = updatedAnswers)
        }
        return answer
    }

    override fun addUser(userId: String, username: String, ): User {
        val user = User(userId , username, emptyList(), emptyList())
        users[userId] = user
        return user
    }


    override fun availableCourses(): List<Course> {
        return courses.values.toList()
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

    override fun getUserQuestions(user: User): List<Question> {
        val userQuestions = questions.filterValues { it.userId == user.userId }.values.toList()
        return userQuestions.toList()
    }

}