# Project 3 - *Twitter*

**Name of your app** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **22** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right
  * [x] User can then enter a new tweet and post this to Twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline
  * [x] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **see embedded image media within a tweet** on list or detail view.

The following **optional** features are implemented:

* [x] User is using **"Twitter branded" colors and styles**
* [x] User sees an **indeterminate progress indicator** when any background or network task is happening
* [x] User can **select "reply" from home timeline to respond to a tweet**
  * [x] User that wrote the original tweet is **automatically "@" replied in compose**
* [x] User can tap a tweet to **open a detailed tweet view**
  * [x] User can **take favorite (and unfavorite) or retweet** actions on a tweet
* [x] User can view more tweets as they scroll with infinite pagination
* [ ] Compose tweet functionality is built using modal overlay
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.org/android/Drawables#vector-drawables) where appropriate.
* [x] User can view following / followers list through any profile they view.
* [x] Use the View Binding library to reduce view boilerplate.
* [ ] On the Twitter timeline, apply scrolling effects such as [hiding/showing the toolbar](http://guides.codepath.org/android/Using-the-App-ToolBar#reacting-to-scroll) by implementing [CoordinatorLayout](http://guides.codepath.org/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events).
* [ ] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.

The following **additional** features are implemented:

* [ ] List anything else that you can get done to improve the app functionality!
* [x] When you like a tweet, go back to timeline, NO NEED TO refresh/reload, click the tweet again, the favorited button persists
* [x] Shared Element Activity Transition from home timeline to details activities.
* [x] Fragments and tabs implemented when viewing followers/following list on details activity

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/flaurencyac/Twitter/blob/main/Twitter.gif' title='First Video Walkthrough' width='' alt='Video Walkthrough' />

<img src='https://github.com/flaurencyac/Twitter/blob/main/extra.gif' title='Second Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with Record It https://www.buildtoconnect.com/en/products/recordit

## Notes

Please note that the gifs are somewhat poor quality because GitHub only allows uploads of up to 25mb.

My TA, Aldo Socarras, is listed as a contributor, because for the "Pre-Work and Setup" module, my TAs and I–for the life of us–could not figure out why my API keys and my OAuth would not work. What happened was that my app would open, go to the Login Activity, go to the Authorization page on Google Chrome, and then it would display "successfully authorized OAuth", but when I returned to my app it immediately told me to repeat autohorization on Chrome. The cycle went on and on, and there wasn't a way to actually "log in." When we used Logcat the error it threw us was that "Log" (the method itself) was not resolvable. We individually looked through all of the config files/settings and my Twitter Developer account settings and found nothing wrong, so next, we individually redid the setup/config and while my TA's worked, mine did not! We decided that since we'd taken upwards of 2 hours on troubleshooting that it would be okay for me to clone his repo (which just had the pre-work configurations/setup) and get on with the first user story. Oddly enough, after I cloned his repo the same issue happened until I used his API keys.

My experience with this project: I loved being able to make the UI from scratch, debugging, and getting to learn how to use REST APIs in this project. After Flixster I feel like I've been able to understand Java and the concepts in Android app development (ie. contexts, async, recycler views, adapters, fragments, design, etc.) more and more comfortably. In particular, with this Twitter app, I had the hardest time handling intents between three activities and found that there is a lot of nuance like, for example, when I wish to choose a tweet, go to the details activity, like the tweet, and return to the timeline and see the like persist in the timeline. 

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 Flaurencya Ciputra

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
