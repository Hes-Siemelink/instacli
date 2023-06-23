package yay.spec

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import yay.test

class DataManipulation {

    @Test
    fun forEachAndMerge() {
        test("data-manipulation/For each and merge.yay")
    }

    @Test
    fun merge() {
        test("data-manipulation/Merge data.yay")
    }

    @Test
    @Disabled
    fun join() {
        test("data-manipulation/Join handler tests.yay")
    }

    @Test
    fun replace() {
        test("data-manipulation/Replace handler tests.yay")
    }

    @Test
    fun objectCreation() {
        test("data-manipulation/Object tests.yaml")
    }
}

