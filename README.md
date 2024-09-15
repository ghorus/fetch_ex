# Project: Fetch Exercise

### Description
This application fetches data from an API: https://fetch-hiring.s3.amazonaws.com/hiring.json

The application features not only the sorting of the extracted data by **"listId"** but also **"name"**. It has additional features such as:
- Font size change
- Dark/Light Mode
- Search

### Challenges
The challenges faced during production were: being new to Kotlin, Android Studio, and slow testing. Additional features that I hope to implement in the future are like such [Fetch Exercise Original Design](https://imgur.com/a/jkmbK7h) as the original only actual turned out like [this](https://imgur.com/a/zisaVnA)

### TLDR of the ecosystem learned: 
- Functions are declared with "fun" and need to specify types for parameters and outputs
- Variables need "var", or "val" which is like "const" in JS.

### Approach
- To fetch API and display on mobile, a successful connection needs to be established using the *"URL"* module and show it on the UI(User interface) via an adapter. This adapter will bind the extracted data, which has been sorted as requested, with the UI.
- The chosen form of display is the *RecyclerView*
- For the additional features, such as searching, we use logic to update the same *RecyclerView*. As for the dark/light mode, we rely on the lines like:

*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)*

And with the font size change feature the primary line is:

*val currentSize = view.textSize / resources.displayMetrics.scaledDensity*
            *view.textSize = currentSize + decreaseBy*

### Result
- All features worked as [intended](https://imgur.com/a/NQQWLh7) (Font size change, Dark/Light mode, Search)

### How to Use
- Simply run the application as usual by pressing the "Run" button icon after downloading this project. Press the sun/moon icons for dark/light mode feature. Press "Smaller Font" or "Bigger Font" to change the font size.
