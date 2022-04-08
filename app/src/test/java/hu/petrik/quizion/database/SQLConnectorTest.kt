package hu.petrik.quizion.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertTrue
import org.junit.Test

internal class SQLConnectorTest{
    @Test
    fun succesfulConnect(){
        runBlocking{
            withContext(Dispatchers.IO){
                val response = SQLConnector.serverCall("GET","quizzes")
                assertTrue(response[0] == "401")
            }
        }
    }

    @Test
    fun notFoundConnect(){
        runBlocking{
            withContext(Dispatchers.IO){
                val response = SQLConnector.serverCall("GET","test")
                assertTrue(response[0] == "404")
            }
        }
    }
}