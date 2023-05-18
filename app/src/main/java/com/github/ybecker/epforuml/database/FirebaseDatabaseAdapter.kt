package com.github.ybecker.epforuml.database

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.github.ybecker.epforuml.MainActivity
import com.github.ybecker.epforuml.UserStatus
import com.github.ybecker.epforuml.database.Model.*
import com.github.ybecker.epforuml.notifications.NotificationType
import com.github.ybecker.epforuml.notifications.PushNotificationService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture


/**
 * This class represents a database that uses Firebase Realtime Database
 */
class FirebaseDatabaseAdapter(instance: FirebaseDatabase) : Database() {

    // save the database reference
    private val dbInstance = instance
    private val db: DatabaseReference = dbInstance.reference

    // save every useful path to navigate in the database
    private val usersPath = "users"
    private val chatsWith = "chatsWith"
    private val coursesPath = "courses"
    private val questionsPath = "questions"
    private val answersPath = "answers"
    private val subscriptionsPath = "subscriptions"
    private val chatsPath = "chats"
    private val notificationsPath = "notifications"

    private val courseIdPath = "courseId"
    private val userIdPath = "userId"
    private val questionIdPath = "questionId"
    private val answerIdPath = "answerId"

    private val receiverIdPath ="receiverId"
    private val textPath = "text"
    private val senderIdPath = "senderId"
    private val datePath = "date"
    private val chatIdPath = "chatId"
    private val likePath = "like"
    private val followersPath = "followers"
    private val endorsedPath = "endorsed"

    private val courseNamePath = "courseName"
    private val usernamePath = "username"

    private val questionTextPath = "questionText"
    private val questionTitlePath = "questionTitle"
    private val answerTextPath = "answerText"
    private val isAnonymousPath = "anonymous"

    private val questionURIPath = "imageURI"
    private val questionAudioPath = "audioPath"

    private val emailPath = "email"
    private val profilePicPath = "profilePic"
    private val userInfoPath = "userInfo"
    private val statusPath = "status"
    private val connectionsPath = "connections"
    private val sharesLocationPath = "sharesLocation"
    private val longitudePath = "longitude"
    private val latitudePath = "latitude"

    override fun availableCourses(): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Course>>()
        // go in "courses" dir
        db.child(coursesPath).get().addOnSuccessListener {
            val courses = mutableListOf<Course>()
            // add every course that in not null in "courses" in the map
            for (courseSnapshot in it.children) {
                val course = getCourse(courseSnapshot)
                if (course != null) {
                    courses.add(course)
                }
            }
            //complete the future when every children has been added
            future.complete(courses)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun getQuestions(): CompletableFuture<List<Question>> {
        val future = CompletableFuture<List<Question>>()
        // go in "questions" dir
        db.child(questionsPath).get().addOnSuccessListener {
            val questions = mutableListOf<Question>()
            // add every question that in not null in "questions" in the map
            for (questionSnapshot in it.children) {
                val question = getQuestion(questionSnapshot)
                if (question != null) {
                    questions.add(question)
                }
            }
            //complete the future when every children has been added
            future.complete(questions)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }


    override fun getAllAnswers(): CompletableFuture<List<Answer>> {
        val future = CompletableFuture<List<Answer>>()
        // go in "courses" dir
        db.child(answersPath).get().addOnSuccessListener {
            val answers = mutableListOf<Answer>()
            // add every course that in not null in "courses" in the map
            for (answerSnapshot in it.children) {
                val answer = getAnswer(answerSnapshot)
                if (answer != null) {
                    answers.add(answer)
                }
            }
            //complete the future when every children has been added
            future.complete(answers)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    //Note that using course.questions in the main is false because you don't take new values in the db into account !
    override fun getCourseQuestions(courseId: String): CompletableFuture<List<Question>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(coursesPath,courseId,questionsPath), questionsPath) { ds -> getQuestion(ds) }
                //cast the future to the a list of question future
                as CompletableFuture<List<Question>>
    }

    //Note that using question.answers in the main is false because you don't take new values in the db into account !
    override fun getQuestionAnswers(questionId: String): CompletableFuture<List<Answer>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(questionsPath,questionId,answersPath), answersPath) { ds -> getAnswer(ds) }
                //cast the future to the a list of answers future
                as CompletableFuture<List<Answer>>
    }

    //Note that using user.question in the main is false because you don't take new values in the db into account !
    override fun getUserQuestions(userId: String): CompletableFuture<List<Question>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(usersPath,userId,questionsPath), questionsPath) { ds -> getQuestion(ds) }
                //cast the future to the a list of questions future
                as CompletableFuture<List<Question>>
    }

    //Note that using user.answers in the main is false because you don't take new values in the db into account !
    override fun getUserAnswers(userId: String): CompletableFuture<List<Answer>> {

        // use private getListOfAny methode with correct arguents
        return getListOfAny(listOf(usersPath,userId,answersPath), answersPath) { ds -> getAnswer(ds) }
                //cast the future to the a list of answers future
                as CompletableFuture<List<Answer>>
    }

    //Note that using user.subscriptions in the main is false because you don't take new values in the db into account !
    override fun getUserSubscriptions(userId: String): CompletableFuture<List<Course>> {
        // use private getListOfAny methode with correct arguents
        return  getListOfAny(listOf(usersPath,userId,subscriptionsPath), coursesPath) { ds -> getCourse(ds) }
                //cast the future to the a list of courses future
                as CompletableFuture<List<Course>>
    }

    override fun getCourseNotificationTokens(courseId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()

        db.child(coursesPath).child(courseId).child(notificationsPath).get().addOnSuccessListener {
            val tokens = mutableListOf<String>()

            for(courseSnapshot in it.children){
                val token = courseSnapshot.value as String
                tokens.add(token)        }
            future.complete(tokens)
        }
        return future
    }

    override fun getCourseNotificationUserIds(courseId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()

        db.child(coursesPath).child(courseId).child(notificationsPath).get().addOnSuccessListener {
            val userIds = mutableListOf<String>()

            for(courseSnapshot in it.children){
                val userId = courseSnapshot.key
                if(userId!=null){
                    userIds.add(userId)
                }
            }
            future.complete(userIds)
        }
        return future
    }

    override fun addCourse(courseName: String): Course {
        // create a space for the new course in db and save its id
        val newChildRef = db.child(coursesPath).push()
        val courseId = newChildRef.key ?: error("Failed to generate course ID")

        // create the new course using given parameters
        val course = Course(courseId, courseName, emptyList(), emptyList())

        // add the new course in the db
        newChildRef.setValue(course)
        return course
    }

    override fun addChat(
        senderId: String,
        receiverId: String,
        text: String?
    ): Chat{
        val newChildRef = db.child(chatsPath).push()
        val chatId = newChildRef.key ?: error("Failed to generate chat ID")
        val chat = Chat(chatId,LocalDateTime.now().toString(),receiverId,senderId,text)
        newChildRef.setValue(chat)
        return chat
    }

    override fun addChatsWith(
        senderId: String,
        receiverId: String,
    ): String{

        ///val newChildRefSender = db.child(usersPath).child(senderId).child(chatsWith).push()
        //val chatIdSender = newChildRefSender.key ?: error("Failed to generate chat ID")
        db.child(usersPath).child(senderId).child(chatsWith).child(receiverId).setValue(receiverId)

        //val newChildRefReceiver = db.child(usersPath).child(receiverId).child(chatsWith).push()
        //val chatIdReceiver = newChildRefReceiver.key ?: error("Failed to generate chat ID")
        db.child(usersPath).child(receiverId).child(chatsWith).child(senderId).setValue(senderId)
        //reference that these users chatted with eachother
        return "chatIdSender"
    }


    override fun getUserId(userName: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        // go in the given path
        db.child(usersPath).get().addOnSuccessListener { it ->
            it.children.forEach{
                if(it.child(usernamePath).value!! == userName){
                    future.complete(getUser(it)!!.userId)
                }
            }
        }
        return future

    }

    override fun getChatsWith(userID: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        db.child(usersPath).child(userID).child(chatsWith).get().addOnSuccessListener{
            val chats =  mutableListOf<String>()
            for(chatSnapshot in it.children){
                    chats.add(chatSnapshot.value!! as String)
                }
            future.complete(chats)
        }.addOnFailureListener{
            future.completeExceptionally(it)
        }
        return future
    }

    override fun addAnswer(userId: String, questionId: String, answerText: String?): Answer {
        // create a space for the new answer in sb and save its id
        val newChildRef = db.child(answersPath).push()
        val answerId = newChildRef.key ?: error("Failed to generate answer ID")
        // create the new answer using given parameters
        val answer = Answer(answerId, questionId, userId, answerText ?: "", emptyList(), "")
        newChildRef.setValue(answer)

        //add the answer in the question's answers list
        db.child(questionsPath).child(questionId).child(answersPath).child(answerId).setValue(answerId)

        //add the answer in the user's questions list
        db.child(usersPath).child(userId).child(answersPath).child(answerId).setValue(answerId)

        return answer
    }

    override fun addUser(userId:String, username: String, email: String): CompletableFuture<User> {
        return addUser(User(userId, username, email))
    }

    override fun addUser(user: User): CompletableFuture<User> {
        val future = CompletableFuture<User>()
        //try to get the user with given id
        getUserById(user.userId).thenAccept {
            if(it != null){
                //if user exists complete with it
                future.complete(it)
            }
            else{
                // if user does not exist add him and complete with it
                db.child(usersPath).child(user.userId).setValue(user)
                future.complete(user)
            }
        }
        return future
    }

    override fun addQuestionFollower(userId: String, questionId: String) {
        db.child(questionsPath).child(questionId).child(followersPath).child(userId).setValue(userId)
    }

    override fun addAnswerLike(userId: String, answerId: String) {
        db.child(answersPath).child(answerId).child(likePath).child(userId).setValue(userId)
    }

    override fun addSubscription(userId: String, courseId: String): CompletableFuture<User?> {

        db.child(usersPath).child(userId).child(subscriptionsPath).child(courseId).setValue(courseId)

        return getUserById(userId)
    }

    override fun removeUser(userId: String) {
        db.child(usersPath).child(userId).removeValue()
    }

    override fun updateUser(user: User) {
        if (user.userId == DatabaseManager.user?.userId) {
            db.child(usersPath).child(user.userId).setValue(user)
        }
    }

    override fun removeSubscription(userId: String, courseId: String) {
        db.child(usersPath).child(userId).child(subscriptionsPath).child(courseId).removeValue()
    }

    override fun addNotification(userId: String, courseId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            db.child(coursesPath).child(courseId).child(notificationsPath).child(userId).setValue(it)
            future.complete(true)
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to retrieve notification token for user $userId and course $courseId", e)
            future.complete(false)
        }

        Firebase.messaging.subscribeToTopic(courseId)
        return future
    }

    override fun removeNotification(userId: String, courseId: String) {
        db.child(coursesPath).child(courseId).child(notificationsPath).child(userId).removeValue()
        Firebase.messaging.unsubscribeFromTopic(courseId)
    }

    override fun removeQuestionFollower(userId: String, questionId: String) {
        db.child(questionsPath).child(questionId).child(followersPath).child(userId).removeValue()
    }

    override fun removeAnswerLike(userId: String, answerId: String) {
        db.child(answersPath).child(answerId).child(likePath).child(userId).removeValue()
    }

    override fun getQuestionById(id: String): CompletableFuture<Question?> =
        //call the private getAnyById with argument for questions
        getAnyById(id, questionsPath) {ds -> getQuestion(ds)}
            //cast the general future to a question one
            as CompletableFuture<Question?>



    override fun getAnswerById(id: String): CompletableFuture<Answer?> =

        //call the private getAnyById with argument for answers
         getAnyById(id, answersPath) {ds -> getAnswer(ds)}
                //cast the general future to a answer one
                as CompletableFuture<Answer?>


    override fun getUserById(id: String): CompletableFuture<User?> =

        //call the private getAnyById with argument for users
        getAnyById(id, usersPath) {ds -> getUser(ds)}
                //cast the general future to a user one
                as CompletableFuture<User?>



    override fun getCourseById(id: String): CompletableFuture<Course?> =

        //call the private getAnyById with argument for courses
        getAnyById(id, coursesPath) { ds -> getCourse(ds) }
                //cast the general future to a course one
                as CompletableFuture<Course?>


    override fun registeredUsers(): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        // go in "courses" dir
        db.child(usersPath).get().addOnSuccessListener {
            val courses = mutableListOf<String>()
            // add every course that in not null in "courses" in the map
            for (userSnapshot in it.children) {
                val user = getUser(userSnapshot)
                if (user != null) {
                    courses.add(user.username)
                }
            }
            //complete the future when every children has been added
            future.complete(courses)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getChat(userId1: String, userId2: String): CompletableFuture<List<Chat>> {
        val future = CompletableFuture<List<Chat>>()
        db.child(chatsPath).get().addOnSuccessListener{
            val chats =  mutableListOf<Chat>()
            for(chatSnapshot in it.children){
                val chat = retrieveChat(chatSnapshot)
                if((chat!!.senderId == userId1 && chat.receiverId == userId2) ||
                    (chat.senderId == userId2 && chat.receiverId == userId1)){
                    chats.add(chat)
                }
            }
            future.complete(chats)
        }.addOnFailureListener{
            future.completeExceptionally(it)

        }
        return future
    }

    override fun getQuestionFollowers(questionId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        db.child(questionsPath).child(questionId).child(followersPath).get().addOnSuccessListener {
            val userIds = mutableListOf<String>()

            for(courseSnapshot in it.children){
                val userId = courseSnapshot.key
                if(userId!=null){
                    userIds.add(userId)
                }
            }
            future.complete(userIds)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getAnswerLike(answerId: String): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()
        db.child(answersPath).child(answerId).child(likePath).get().addOnSuccessListener {
            val userIds = mutableListOf<String>()

            for(courseSnapshot in it.children){
                val userId = courseSnapshot.key
                if(userId!=null){
                    userIds.add(userId)
                }
            }
            future.complete(userIds)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun setUserPresence(userId: String) {
        val database = Firebase.database
        val connectionRef =
            database.getReference("$usersPath/$userId/$connectionsPath")

        val connectionStateRef = database.getReference(".info/connected")
        connectionStateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    val con = connectionRef.push()

                    // When this device disconnects set it to false
                    con.onDisconnect().removeValue()

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(java.lang.Boolean.TRUE)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled at .info/connected")
            }
        })
    }

    override fun removeUserConnection(userId: String) {
        val connectionsRef =
            Firebase.database.getReference("$usersPath/$userId/$connectionsPath")
        connectionsRef.removeValue()
    }

    override fun getUserStatus(userId: String, courseId: String): CompletableFuture<UserStatus?> {
        val future = CompletableFuture<UserStatus?>()
        db.child(usersPath).child(userId).child(statusPath).child(courseId).get().addOnSuccessListener {
            val statusString = it.value as String?
            if(statusString != null) {
                future.complete(UserStatus.valueOf(statusString))
            } else{
                future.complete(null)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getAnswerEndorsement(answerId: String): CompletableFuture<String?> {

        val future = CompletableFuture<String?>()
        db.child(answersPath).child(answerId).child(endorsedPath).get().addOnSuccessListener{
            future.complete(it.value as String?)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun addAnswerEndorsement(answerId: String, username: String){
        db.child(answersPath).child(answerId).child(endorsedPath).setValue(username)
    }

    override fun removeAnswerEndorsement(answerId: String) {
        db.child(answersPath).child(answerId).child(endorsedPath).removeValue()
    }

    override fun getOtherUsers(userId: String): CompletableFuture<List<User>> {
        val future = CompletableFuture<List<User>>()
        // go in "users" dir
        db.child(usersPath).get().addOnSuccessListener {
            val users = mutableListOf<User>()
            // add every user that is not null and not equal to userId to the users list
            for (userSnapshot in it.children) {
                val user = getUser(userSnapshot)
                if (user != null && user.userId != userId) {
                    users.add(user)
                }
            }
            //complete the future when every children has been added
            future.complete(users)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun updateLocalization(userId: String, position: LatLng, sharesLocation: Boolean) {
        db.child(usersPath).child(userId).child(longitudePath).setValue(position.longitude)
        db.child(usersPath).child(userId).child(latitudePath).setValue(position.latitude)
        db.child(usersPath).child(userId).child(sharesLocationPath).setValue(sharesLocation)
    }

    override fun addStatus(userId: String, courseId: String, status: UserStatus) {
        db.child(usersPath).child(userId).child(statusPath).child(courseId).setValue(status.name)
    }

    override fun removeStatus(userId: String, courseId: String) {
        db.child(usersPath).child(userId).child(statusPath).child(courseId).removeValue()
    }



    private fun getAnyById(id: String, dataPath: String, getter: (DataSnapshot) -> Any?): CompletableFuture<Any?> {
        val future = CompletableFuture<Any?>()
        // go in the given path
        db.child(dataPath).child(id).get().addOnSuccessListener {
            // use the getter methode to get the researched object and complete given future with it
            future.complete(getter(it))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    private fun getListOfAny(listPath: List<String>, objectPath: String, getter: (DataSnapshot) -> Any?): CompletableFuture<List<Any>>{

        val future = CompletableFuture<List<Any>>()
        //create path by joining the given list
        val path = listPath.joinToString(separator = "/")
        db.child(path).get().addOnSuccessListener {
            val modelFutures = mutableListOf<CompletableFuture<Any?>>()
            // add every user's subscribed course that is not null in the map
            for (subscriptionSnapshot in it.children) {
                //create a future for every child
                val modelId = subscriptionSnapshot.value as String
                //get object for every child
                modelFutures.add(getAnyById(modelId,objectPath, getter))
            }
            convertListOfFutureToFutureOfList(future, modelFutures)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    private fun convertListOfFutureToFutureOfList(future: CompletableFuture<List<Any>>, futureList: List<CompletableFuture<Any?>>){
        val models = mutableListOf<Any>()
        // when every child's future complete, crete the list of models with their result
        CompletableFuture.allOf(*futureList.toTypedArray()).thenAccept {
            for (modelFuture in futureList) {
                if (!modelFuture.isCompletedExceptionally) {
                    val model = modelFuture.get()
                    if(model != null) {
                        models.add(model)
                    }
                }
            }
            //complete the  caller function future
            future.complete(models)
        }
    }

    private fun getUser(dataSnapshot: DataSnapshot): User? {

        // Get user id
        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        // Get username
        val username = dataSnapshot.child(usernamePath).getValue(String::class.java)

        // Get email
        val email = dataSnapshot.child(emailPath).getValue(String::class.java)

        // save every answers in a List using getAnswers private methode
        val answers = arrayListOf<String>()
        dataSnapshot.child(answersPath).children.forEach { answerSnapshot ->
            answerSnapshot.key?.let { answers.add(it) }
        }
        // save every question in a List using getQuestion private method
        val questions = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }
        // save every subscriptions in a List using getCourse private method
        val subscriptions = arrayListOf<String>()
        dataSnapshot.child(subscriptionsPath).children.forEach {subscriptionSnapshot ->
            subscriptionSnapshot.key?.let { subscriptions.add(it) }
        }

        val chatsWith = arrayListOf<String>()
        dataSnapshot.child("chatsWith").children.forEach {chatsWithSnapshot -> chatsWithSnapshot.key?.let {
            chatsWith.add(
                it
            )
        }
        }

        // Get profile picture
        val profilePic = dataSnapshot.child(profilePicPath).getValue(String::class.java)

        // Get user infos
        val userInfo = dataSnapshot.child(userInfoPath).getValue(String::class.java)

        // Get user status (is assistant or teacher)
        val status = arrayListOf<String>()
        dataSnapshot.child(statusPath).children.forEach { conSnapshot ->
            conSnapshot.key?.let { status.add(it) }
        }

        // Get user connection status (is connected or not)
        val connections = arrayListOf<Boolean>()
        dataSnapshot.child(connectionsPath).children.forEach { conSnapshot ->
            conSnapshot.key?.let { connections.add(it.toBoolean()) }
        }

        // Get whether the user is sharing his location
        val sharesLocation = dataSnapshot.child(sharesLocationPath).getValue(Boolean::class.java)

        // Get user's coordinates
        val longitude = dataSnapshot.child(longitudePath).getValue(Double::class.java)
        val latitude = dataSnapshot.child(latitudePath).getValue(Double::class.java)

        if(userId!=null && username!=null && email!=null){
            return User(
                userId,
                username,
                email,
                questions,
                answers,
                subscriptions,
                chatsWith,
                profilePic ?: "",
                userInfo ?: "",
                status,
                connections,
                sharesLocation ?: false,
                longitude ?: -200.0,
                latitude ?: -200.0
            )
        }
        return null
    }

    private fun getQuestion(dataSnapshot: DataSnapshot): Question? {

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java)

        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java)

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        val isAnonymous = dataSnapshot.child(isAnonymousPath).getValue(Boolean::class.java)

        val questionTitle = dataSnapshot.child(questionTitlePath).getValue(String::class.java)

        val questionText = dataSnapshot.child(questionTextPath).getValue(String::class.java)

        val questionURI = dataSnapshot.child(questionURIPath).getValue(String::class.java)

        // save every answers in a List using getAnswers private method
        val answers = arrayListOf<String>()
        dataSnapshot.child(answersPath).children.forEach { answerSnapshot ->
            answerSnapshot.key?.let { answers.add(it) }
        }

        val followers = arrayListOf<String>()
        dataSnapshot.child(followersPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { followers.add(it) }
        }

        val audioPath = dataSnapshot.child(questionAudioPath).getValue(String::class.java)


        if(questionId!=null && courseId!=null && userId!=null&& isAnonymous!=null && questionTitle!=null && questionText!=null && questionURI!=null&& audioPath!=null){
            return Question(questionId, courseId, userId,isAnonymous,  questionTitle, questionText, questionURI, answers, followers, audioPath)

        }
        return null
    }

    private fun getAnswer(dataSnapshot: DataSnapshot): Answer?{
        // save every variables of the answer in a map
        val answerId = dataSnapshot.child(answerIdPath).getValue(String::class.java)

        val questionId = dataSnapshot.child(questionIdPath).getValue(String::class.java)

        val userId = dataSnapshot.child(userIdPath).getValue(String::class.java)

        val answerText = dataSnapshot.child(answerTextPath).getValue(String::class.java)

        val like = arrayListOf<String>()
        dataSnapshot.child(this.likePath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { like.add(it) }
        }

        val endorsement = dataSnapshot.child(answerTextPath).getValue(String::class.java)

        if(answerId!=null && questionId!=null && userId!=null && answerText != null && endorsement != null){
            return Answer(answerId, questionId, userId, answerText, like, endorsement)
        }

        return null
    }

    private fun getCourse(dataSnapshot: DataSnapshot): Course?{
        // save every non list variables of the course in a map
        val courseId = dataSnapshot.child(courseIdPath).getValue(String::class.java)

        val courseName = dataSnapshot.child(courseNamePath).getValue(String::class.java)

        // save every questions in a List using getQuestion private method
        val questions = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }

        val notifications = arrayListOf<String>()
        dataSnapshot.child(questionsPath).children.forEach { questionSnapshot ->
            questionSnapshot.key?.let { questions.add(it) }
        }

        if(courseId!=null && courseName!=null){
            return Course(courseId, courseName, questions, notifications)
        }

        return null
    }

    private fun retrieveChat(dataSnapshot: DataSnapshot): Chat?{
        val chatId = dataSnapshot.child(chatIdPath).getValue(String::class.java)
        val senderId = dataSnapshot.child(senderIdPath).getValue(String::class.java)
        val receiverId = dataSnapshot.child(receiverIdPath).getValue(String::class.java)
        val text = dataSnapshot.child(textPath).getValue(String::class.java)
        val date = dataSnapshot.child(datePath).getValue(String::class.java)
        if(senderId!=null && receiverId!=null){
            return Chat(chatId,date,receiverId,senderId,text)
        }

        return null
    }
    override fun removeChat(chatId: String): Boolean {
        db.child(chatsPath).child(chatId).removeValue()
        return true
    }

    override fun addQuestion(userId: String, courseId: String,isAnonymous: Boolean, questionTitle: String, questionText: String?, image_uri: String, audioPath : String): CompletableFuture<Question> {
        val questionFuture = CompletableFuture<Question>()

        // create a space for the new question in db and save its id
        val newChildRef = db.child(questionsPath).push()
        val questionId = newChildRef.key ?: error("Failed to generate question ID")
        // create the new question using given parameters
        var futureURI = CompletableFuture.completedFuture("")
        if (image_uri != "null") {
            futureURI = uploadImageToFirebase(image_uri)
        }
        var futureAudio = CompletableFuture.completedFuture("")
        if(audioPath != "null"){
            futureAudio = uploadAudioToFirebase(audioPath)
        }
        CompletableFuture.allOf(futureAudio,futureURI).thenAccept{_ ->
            val question = Question(questionId, courseId, userId,isAnonymous, questionTitle, questionText ?: "", futureURI.get(), emptyList(), emptyList(), futureAudio.get())

            // add the new question in the db
            newChildRef.setValue(question)
            questionFuture.complete(question)
            //add the question in the course's questions list
            db.child(coursesPath).child(courseId).child(questionsPath).child(questionId).setValue(questionId)
            //add the question in the user's questions list
            db.child(usersPath).child(userId).child(questionsPath).child(questionId).setValue(questionId)

            if(isAnonymous){
                PushNotificationService().sendNotification(MainActivity.context,"someone Anonymous", questionTitle, questionText ?: "", courseId, NotificationType.QUESTION)
            } else {
                this.getUserById(userId).thenAccept {
                    PushNotificationService().sendNotification(MainActivity.context,it?.username?: "someone", questionTitle, questionText ?: "", courseId, NotificationType.QUESTION)
                }
            }
        }
        return questionFuture
    }
    private fun uploadImageToFirebase(uri: String) : CompletableFuture<String>{
        val url = CompletableFuture<String>()

        if(uri == "" || uri.equals(null)){
            url.complete("")

        } else {
            val fileRef: StorageReference =
                FirebaseStorage.getInstance("gs://epforuml-38150.appspot.com").reference.child(System.currentTimeMillis().toString() + getExtension(uri))
            fileRef.putFile(Uri.parse(uri)).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    url.complete(uri.toString())
                }
            }.addOnFailureListener {
                url.completeExceptionally(it)
            }.addOnCanceledListener {
                url.completeExceptionally(RuntimeException("THIS GOT CANCELED"))
            }

        }
        return url
    }

    private fun uploadAudioToFirebase(path: String) : CompletableFuture<String>{
        val url = CompletableFuture<String>()
        if(path == "" || path.equals(null)){
            url.complete("")

        }else{
            val fileRef: StorageReference =
                FirebaseStorage.getInstance("gs://epforuml-38150.appspot.com").reference.child(System.currentTimeMillis().toString() + ".mp4")
            fileRef.putFile(File(path).toUri()).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    url.complete(uri.toString())
                }
            }.addOnFailureListener {
                url.completeExceptionally(it)
            }.addOnCanceledListener {
                url.completeExceptionally(RuntimeException("THIS GOT CANCELED"))
            }

        }
        return url
    }

    private fun getExtension(uri: String): String {
        if (uri.contains("images")) {
                return ".jpeg"
        }
        return ".mp4"
    }

}