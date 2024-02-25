package io.github.vshnv.adapt

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewbinding.ViewBinding
import io.github.vshnv.adapt.databinding.LayoutTextItemBinding
import io.github.vshnv.adapt.dsl.ViewSource
import io.github.vshnv.adapt.dsl.adapt
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.internal.matchers.TypeSafeMatcher
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AdaptBindingTest {
    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testRecyclerViewAttachAdapter() {
        onView(withId(R.id.rv_test)).perform(object : ViewAction {
            override fun getDescription() = "Bind adapt"

            override fun getConstraints() = withId(R.id.rv_test)

            override fun perform(uiController: UiController?, view: View?) {
                val recyclerView = view as RecyclerView
                recyclerView.adapter = adapt<String> {
                    create {
                        ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(LayoutInflater.from(view.context)), ViewBinding::getRoot)
                    }.bind {
                        binding.tvTest.text = data
                    }
                }
            }
        })
    }

    @Test
    fun testAdapterBindsData() {
        val idlingResource = CountingIdlingResource("Adapter Setup")
        IdlingRegistry.getInstance().register(idlingResource)
        idlingResource.increment()
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_test)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = adapt<String> {
                create {
                    ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(LayoutInflater.from(activity)), ViewBinding::getRoot)
                }.bind {
                    binding.tvTest.text = data
                }
            }

            recyclerView.adapter = adapter
            adapter.submitData(listOf("Test 0", "Test 1")) {
                idlingResource.decrement()
            }
        }
        onIdle()
        onView(nthChildOf(withId(R.id.rv_test), 0))
            .check(matches(withText("Test 0")))
        onView(nthChildOf(withId(R.id.rv_test), 1))
            .check(matches(withText("Test 1")))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testAdapterBindsToLifecycle() {
        val idlingResource = CountingIdlingResource("Adapter Setup")
        IdlingRegistry.getInstance().register(idlingResource)
        idlingResource.increment()
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_test)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            var count = 0
            val adapter = adapt<String> {
                create {
                    ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(LayoutInflater.from(activity)), ViewBinding::getRoot)
                }.bind {
                    binding.tvTest.text = data
                }.withLifecycle {
                    binding.tvTest.tag = "Lifecycle Bound"
                    count++
                }
            }
            recyclerView.adapter = adapter
            adapter.submitData(listOf("Test 0", "Test 1")) {
                idlingResource.decrement()
            }
        }
        onIdle()
        onView(nthChildOf(withId(R.id.rv_test), 0))
            .check(matches(withTagValue(`is`("Lifecycle Bound"))))
        onView(nthChildOf(withId(R.id.rv_test), 1))
            .check(matches(withTagValue(`is`("Lifecycle Bound"))))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testAdapterManagesViewholderLifecycle() {
        val idlingResource = CountingIdlingResource("Adapter Setup")
        IdlingRegistry.getInstance().register(idlingResource)
        idlingResource.increment()
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_test)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            var count = 0
            val adapter = adapt<String> {
                create {
                    ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(LayoutInflater.from(activity)), ViewBinding::getRoot)
                }.bind {
                    binding.tvTest.text = data
                }.withLifecycle {
                    it.lifecycle.addObserver(object : LifecycleEventObserver {
                        var gotPaused = false
                        override fun onStateChanged(
                            source: LifecycleOwner,
                            event: Lifecycle.Event
                        ) {
                            if (gotPaused && event == Lifecycle.Event.ON_RESUME) {
                                count++
                                Log.d("TestLifecyceRENEW", "LIFE RENEWED event:${event.name} data:${data} index:${index}")
                                gotPaused = false
                                Log.d("RecycleCountREN", "renewed ${count} times")
                            }
                            if (event == Lifecycle.Event.ON_PAUSE) {
                                gotPaused = true
                            }
                            Log.d("TestLifecyce", "event:${event.name} data:${data} index:${index}")
                        }
                    })
                }
            }
            //recyclerView.recycledViewPool.setMaxRecycledViews(0, 5)

            recyclerView.adapter = adapter
            adapter.submitData((0..100).map { "Test $it" }) {
                idlingResource.decrement()
            }
        }
        onIdle()
        repeat(10) {
            onView(withId(R.id.rv_test))
                .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(20))
            onView(withId(R.id.rv_test))
                .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
        }
        onView(withId(R.id.rv_test))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(5))
        onView(withId(R.id.rv_test))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(10))

        onView(withId(R.id.rv_test))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(5))
        onView(withId(R.id.rv_test))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(20))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    fun nthChildOf(parentMatcher: Matcher<View?>, childPosition: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("with $childPosition child view of type parentMatcher")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view?.parent !is ViewGroup) {
                    return parentMatcher.matches(view?.parent)
                }
                val group = view.parent as ViewGroup
                return (parentMatcher.matches(view.parent)) && group.getChildAt(childPosition) == view
            }
        }
    }

}