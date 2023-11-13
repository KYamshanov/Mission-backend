package ru.kyamshanov.mission.point.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MovableLinkedListTest {

    @Test()
    fun test1() {
        val inputArray = listOf("1", "2", "3", "4")
        val list = MovableLinkedList(inputArray)
        assertEquals(listOf("1", "2", "3", "4"), list.getList())
    }

    @Test()
    fun test2() {
        val inputArray = listOf("1", "2", "3", "4", "5")
        val list = MovableLinkedList(inputArray)
        assertEquals(listOf("1", "2", "3", "4", "5"), list.getList())
    }

    @Test()
    fun test3() {
        val inputArray = listOf("1", "2", "3", "4")
        val list = MovableLinkedList(inputArray)
        list.move("4", "2")
        assertEquals(listOf("1", "4", "2", "3"), list.getList())
    }

    @Test()
    fun test4() {
        val inputArray = listOf("1", "2", "3", "4", "5")
        val list = MovableLinkedList(inputArray)
        list.move("4", "2")
        list.move("1", "3")
        assertEquals(listOf("4", "2", "1", "3", "5"), list.getList())
    }


    @Test()
    fun test6() {
        val inputArray = listOf("1", "2", "3", "4", "5")
        val list = MovableLinkedList(inputArray)
        list.move("1", "5")
        assertEquals(listOf("2", "3", "4", "1", "5"), list.getList())
    }

    @Test()
    fun test7() {
        val inputArray = listOf("c", "2", "3", "4", "m3", "m1", "m2", "8", "9", "10")
        val list = MovableLinkedList(inputArray)
        list.move("c", "m2")
        list.move("m1", "c")
        assertEquals(listOf("2", "3", "4", "m3", "m1", "c", "m2", "8", "9", "10"), list.getList())
    }


    @Test()
    fun test8() {
        val inputArray = listOf("1", "2", "3", "4", "5")
        val list = MovableLinkedList(inputArray)
        list.move("5", "1")
        assertEquals(listOf("5", "1", "2", "3", "4"), list.getList())
    }


    @Test()
    fun test9() {
        val inputArray = listOf("1", "2", "3", "4", "5")
        val list = MovableLinkedList(inputArray)
        list.move("5", "1")
        list.moveInTail("2")
        assertEquals(listOf("5", "1", "3", "4", "2"), list.getList())
    }


    @Test()
    fun test10() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6")
        val list = MovableLinkedList(inputArray)
        list.move("4", "3")
        //   list.move("2", "4")
        //  list.move("3", "5")

        list.move("5", "2")
        //   list.move("1", "5")
        //    list.moveInTail("3")

        list.move("3", "4")
        //  list.move("2", "3")
        list.move("4", "6")

        list.move("1", "3")
        list.move("2", "1")
        //list.moveInTail("4")

        list.move("4", "2")
        list.move("5", "4")
        list.move("3", "6")
        assertEquals(listOf("5", "4", "2", "1", "3", "6"), list.getList())
    }

    @Test()
    fun large_test() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val list = MovableLinkedList(inputArray)
        list.move("5", "1")
        list.move("1", "8")
        list.move("4", "5")
        list.move("5", "2")

        assertEquals(listOf("4", "5", "2", "3", "6", "7", "1", "8", "9", "10"), list.getList())
    }


    @Test()
    fun optimize_large_test() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val list = MovableLinkedList(inputArray)
        //  list.move("5", "1")
        list.move("4", /*"5" */"1")
        list.move("1", "8")
        // list.move("4", "5" "1")
        list.move("5", "2")

        assertEquals(listOf("4", "5", "2", "3", "6", "7", "1", "8", "9", "10"), list.getList())
    }


    @Test()
    fun large_test2() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val list = MovableLinkedList(inputArray)
        list.move("1", "5")
        list.move("8", "1")
        list.move("5", "4")
        list.move("2", "5")
        list.move("3", "7")
        list.move("4", "5")
        list.move("2", "5")

        assertEquals(listOf("4", "2", "5", "8", "1", "6", "3", "7", "9", "10"), list.getList())
    }

    @Test()
    fun optimize_large_test2() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val list = MovableLinkedList(inputArray)
        list.move("1", "5")
        list.move("8", "1")
        list.move("5", "4")
//        list.move("2", "5")
        list.move("3", "7")
        list.move("4", "5")
        list.move("2", "5")

        assertEquals(listOf("4", "2", "5", "8", "1", "6", "3", "7", "9", "10"), list.getList())
    }

    @Test()
    fun large_test3() {
        val inputArray = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        val list = MovableLinkedList(inputArray)
        list.move("1", "5")
        list.move("8", "1") // 2 3    4 8 1 5    6 7 9 10
        list.move("5", "4") // 2 3    5 4 8 1    6 7 9 10
        list.move("3", "7") // 2      5 4 8 1     6 3 7 9
        list.move("4", "5") // 2     4 5 8 1    6 3 7 9 10
        list.move("2", "5") //        4 2 5 8 1    6 3 7 9 10
        list.move("5", "7") //       4 2 8 1      6 3 5 7 9 10

        assertEquals(listOf("4", "2", "8", "1", "6", "3", "5", "7", "9", "10"), list.getList())
    }

}