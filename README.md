# EighthBridge

*Interactive graph modeling and rendering for ScalaFX*


## Introduction


EighthBridge is a ScalaFX library dedicated to:

* **graph modeling**, via its *Graph* and *GraphComponent* traits

* **graph interactive rendering**, via a ScalaFX component named *GraphCanvas* and the related classes and traits - in particular, *VisualGraph*. Graphs are rendered in JavaFX, letting the developer choose how users can interact with every element


EighthBridge stems from both the old Arcontes library (introduced to support a previous version of GraphsJ) and my experience with the [Elm](http://elm-lang.org/) language - which I studied while creating my first HTML 5 videogame - [Solvenius](http://gianlucacosta.info/solvenius/).

In particular, EighthBridge's most important aspect is that *the graph model is immutable* - adding a vertex, for example, creates a new graph.

Such solution, derived from *purely functional programming*, definitely increased the conciseness, simplicity and robustness of the model, dramatically speeding up development.

Mutability is still present where more natural and sometimes almost required - in the ScalaFX components - making EighthBridge a hybrid OOP-functional library, perfectly supported by Scala's hybrid, very elegant nature.


## Requirements

Scala 2.11.8 or later and Java 8u65 or later are recommended to employ EighthBridge.


## Referencing the library

EighthBridge is available on [Hephaestus](https://bintray.com/giancosta86/Hephaestus) and can be declared as a Gradle or Maven dependency; please refer to [its dedicated page](https://bintray.com/giancosta86/Hephaestus/EighthBridge).

Alternatively, you could download the JAR file from Hephaestus and manually add it to your project structure.

Finally, EighthBridge is also a standard [OSGi](http://www.slideshare.net/giancosta86/introduction-to-osgi-56290394) bundle which you can employ in your OSGi architectures! ^\_\_^


## Core concepts

EighthBridge is designed to be simple and minimalist; however, we could now go through a brief overview of the most important traits and classes:

* **Graph** is the fundamental trait underlying the whole library, and is *immutable*: its methods are either query functions or operations creating a new graph instance

* **Vertex**: trait modeling a generic vertex. Immutable.

* **Link**: trait modeling a generic. Immutable.

* **Binding**: since vertexes and links are immutable and exist a-priori, how to join them? Via a *Binding* object, which represents the juncture between a link and a collection of vertexes. The provided implementations are **EdgeBinding** and **ArcBinding**, for point-to-point connections. Bindings are immutable, too.

* **GraphComponent**: the trait shared by vertexes, links and bindings - it provides a unique UUID to identify each component

* **VisualGraph**: the most important *Graph* subtrait, which is widely employed both in the rendering subsystem and in other projects - such as [GraphsJ](https://github.com/giancosta86/GraphsJ): its vertexes and links inherit from **VisualVertex** and **VisualLink**. Bindings for a visual graph are always **ArcBinding**'s - because when you draw a graph you implicitly work with arcs; however, it can easily be interpreted as an undirected graph, as shown in [Prim's Shortest Spanning Tree implementation](https://github.com/giancosta86/GraphsJ-scenarios).

* **GraphCanvas**: the core ScalaFX component for interactive rendering. On construction, it requires a **GraphCanvasController** - telling how to draw graph components as JavaFX nodes and how to handle user interactions - as well as an initial *VisualGraph*, which gets replaced by new instances as the controller provides them in response to the user.

* **DefaultVisualGraph**, **DefaultVisualVertex** and **DefaultVisualLink** are default implementations of the visual traits - each having a related **.*Settings** case class to describe its visual appearance.

* **fx.canvas.basic** is a package providing default implementations of the ScalaFX nodes for rendering graph components, as well as **BasicController**, a fine-grained controller employed by such components to control user interaction.

* **fx.canvas.basic.editing** is a package containing the utility trait **InteractiveEditingController** and its sub-traits

* **util**: package with a wide range of utilities - especially related to alerts and input dialogs, geometric computations and desktop integration


For further information, a basic documentation can be found in its Scaladoc, which can be downloaded from the library's [section in Hephaestus](https://bintray.com/giancosta86/Hephaestus/EighthBridge). Finally, the full open source code is available on GitHub.


## About the name

The name *EighthBridge* derives from the famous [problem of the 7 bridges of Königsberg](https://en.wikipedia.org/wiki/Seven_Bridges_of_K%C3%B6nigsberg), negatively solved by Leonhard Euler: more precisely, the famous mathematician introduced the concept of *graph* to demonstrate that the problem had no solution; however, the problem can be positively solved if, for example, we think out of the box and introduce... a suitable eighth bridge! ^\_\_^


## Further references

* [GraphsJ](https://github.com/giancosta86/GraphsJ)

* [Scala](http://scala-lang.org/)

* [ScalaFX](http://scalafx.org/)

* [Elm](http://elm-lang.org/)
