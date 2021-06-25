# Klessify

#### Contributors:
#### 2020/2021
- Jelle Becirspahic
- Bart Engels
- Marijke Eggink

###Overview
Klessify is a webapplication that utilizes the Weka API to make machine learning
more approachable to people with little knowledge of machine learning.
With a clean Bootstrap design and an explainatory approach, beginners will
face a less challenging start to their machine learning experience.

### The project
This web application was made in Java using the [Springboot framework](https://spring.io/projects/spring-boot) and
[Weka API](https://waikato.github.io/weka-wiki/using_the_api/). Springboot is used
as a web controller while the Weka API was used to create machine learning
models.

### Setting up the project
This project requires you to create 3 directories.
- Weka demo file directory. This holds the Weka demo ARFF's. These are included in the Weka desktop application which
  can be downloaded [here](https://waikato.github.io/weka-wiki/downloading_weka/). When the installation is done navigate to 
  the data folder and copy and paste these to your created folder.
- Temporary user file directory. This is an empty directory.
- Temporary serialization object directory. This is an empty directory.

Once you have set up these three directories open this project in your
IDE and navigate to *src/main/resources/application.properties*. This file
contains references to the three directories you created.
- *example.data.path* references the weka demo files.
- *temp.data.path* and *spring.servlet.multipart.location* refer to the directory where temporary user files are stored.
- *tmp.filePath* refers to the temporary serialization object directory.

Besides that, the *application.properties* file also contains maximum size of files that can be uploaded 
and youtube links to video tutorials explaining the machine learning algorithms that are implemented in this project.
These can and should eventually be changed when the application is expanded by adding
more algorithms or when the files that can be uploaded should be able to be bigger.
