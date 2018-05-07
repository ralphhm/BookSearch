# Book Search
Sample Android Application written in Kotlin to search for books using [OpenLibrary API](https://openlibrary.org/developers/api) with Master/Detail views.
Architecture Components ViewModel is used to implement MVVM and support orientation change without losing UI state.
## Dependencies
* [Retrofit 2](http://square.github.io/retrofit)
* [Fresco](http://frescolib.org)
* [RxJava 2](https://github.com/ReactiveX/RxJava)
* [Groupie](https://github.com/lisawray/groupie)
* [Dagger 2](https://google.github.io/dagger/)
* [Android Architecture Components (ViewModel/LiveData)](https://developer.android.com/topic/libraries/architecture)
## Improvements
* Add (remote) logging of error cases
* Differentiate network errors (where retry makes sense) and runtime exceptions (where something is wrong with the chain) in SearchViewModel
