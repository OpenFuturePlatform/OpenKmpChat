//package com.mutualmobile.harvestKmp.android.viewmodels
//
//import com.mutualmobile.harvestKmp.android.ui.MainActivity
//
//class AppUITest {
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<MainActivity>()
//
//    @Test
//    fun testAboutButtonExistence() {
//        composeTestRule
//            .onNodeWithContentDescription("aboutButton")
//            .assertIsDisplayed()
//    }
//
//    @Test
//    fun testOpeningAndClosingAboutPage() {
//        composeTestRule
//            .onNodeWithContentDescription("aboutButton")
//            .performClick()
//
//        composeTestRule
//            .onNodeWithText("About Device")
//            .assertIsDisplayed()
//
//        composeTestRule
//            .onNodeWithContentDescription("Up Button")
//            .performClick()
//
//        composeTestRule
//            .onNodeWithText("Reminders")
//            .assertIsDisplayed()
//    }
//}