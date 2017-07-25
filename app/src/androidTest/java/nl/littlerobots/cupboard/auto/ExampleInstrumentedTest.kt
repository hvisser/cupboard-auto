package nl.littlerobots.cupboard.auto

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import nl.littlerobots.cupboard.auto.model.Cheese
import nl.qbusict.cupboard.CupboardFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    lateinit var helper: MyOpenHelper

    @Before
    fun setup() {
        helper = MyOpenHelper(InstrumentationRegistry.getTargetContext(), null)
    }


    @Test
    fun testOpenDatabase() {
        val db = helper.writableDatabase
    }

    @Test
    fun testPutValue() {
        val db = helper.writableDatabase
        val cheese = Cheese.create(null, "Gouda")
        val id = CupboardFactory.cupboard().withDatabase(db).put(cheese)
        Assert.assertNotNull(id)
        val cursor = db.query("cheese", null, "_id = ?", arrayOf(id.toString()), null, null, null, null)
        val index = cursor.getColumnIndexOrThrow("name")
        Assert.assertTrue(cursor.moveToNext())
        Assert.assertEquals(1, index)
        Assert.assertEquals("Gouda", cursor.getString(index))
        cursor.close()
    }

    @Test
    fun testPutAndGet() {
        val db = helper.writableDatabase
        val cheese = Cheese.create(null, "Gouda")
        val id = CupboardFactory.cupboard().withDatabase(db).put(cheese)

        val storedCheese = CupboardFactory.cupboard().withDatabase(db).get(Cheese::class.java, id)
        Assert.assertNotNull(storedCheese)
        Assert.assertEquals(id, storedCheese._id())
        Assert.assertEquals("Gouda", storedCheese.name())
    }
}
