https://docs.google.com/document/d/1xu_68OwM1HaPIP7-uu7-ZJw4eiGvN3uHw3aXBjIz9n8/edit#
https://forms.gle/isuv2mJN9nbi6HG79


q1: In your own words, explain what the three layer architecture is. Explain what each layer is for.

The presentation is the most user facing element where there's things like home pages
and UI elements
Application layer is where requests and any neccesary calculations are performe
Data layer: where the data is stored and (sometimes) mutated usually just to pull data (like a SQL server)

q2: In your own words, explain what the benefits of using the APIs are.

Allows us to use complexx logic that would take a long time for us to imlement or in some cases be impossible
due to costs or knowledge limiations. Allows us to add these features without having to do it outself

q3: Look back at the scenario from your SayIt Assistant Project Description:

While Helen really likes the desktop version of SayIt Assistant, she’s excited that the 
company is planning to release the web and mobile versions in a few months 
so that she could use SayIt Assistant anywhere and doesn’t need to always use her laptop. 
She is glad that the company has promised that the web and mobile versions 
will work the same as the desktop version and she won’t have to learn how to use the 
app all over again.

Would the company need to reimplement the whole app from scratch for 
each version? Explain your reasoning (based on what we learned in this lab).

No Since our model is decoupled we would only need to recreate the frontend since most of our
data and logic are handeled on the backend and middelware, thin client style