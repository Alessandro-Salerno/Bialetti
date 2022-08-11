[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
![](https://tokei.rs/b1/github/Alessandro-Salerno/Bialetti)


<!-- PROJECT LOGO -->
<p align="center">
<h1 align="center">Bialetti</h1>

  <p align="center">
    Bialetti is a Java networking library meant to abstract away some of the lower level parts of using TCP Sockets.
    <br />
    <br />
    <a href="https://github.com/Alessandro-Salerno/Bialetti/issues">Report Bug</a> ||
    <a href="https://github.com/Alessandro-Salerno/Bialetti/pulls">Request Feature</a>
  </p>

<div align="center">
  <img src=".github/Bialetti.png" alt="Snow" width="900">
</div>

## How to build the JAR File
Bialetti uses [Gradle](https://gradle.org) as its build system.
As such, you just need to choose the `jar` task in Gradle (Either via the commandline or your IDE of choice) and get the output in `build/libs`.

## How to use the library
To use Bialetti, you're going to need to import the `jar` file and the `bialetti` package.

### Creating a simple server application
To create a server application, you're going to need the following components:
* A `BialettiServer` child-class
* A Client Representation
* A Connection Event Handler
* A Server Event Handler
* An Exception Handler

Bialetti provides easy-to-use interfaces and classes for each of these.
Up-to-date documentation will be provided soon.

<!-- LICENSE -->

## License

Distributed under the MIT license. See `LICENSE` for more information.

[contributors-shield]: https://img.shields.io/github/contributors/Alessandro-Salerno/Bialetti.svg?style=flat-square
[contributors-url]: https://github.com/Alessandro-Salerno/Bialetti/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Alessandro-Salerno/Bialetti.svg?style=flat-square
[forks-url]: https://github.com/Alessandro-Salerno/Bialetti/network/members
[stars-shield]: https://img.shields.io/github/stars/Alessandro-Salerno/Bialetti.svg?style=flat-square
[stars-url]: https://github.com/Alessandro-Salerno/Bialetti/stargazers
[issues-shield]: https://img.shields.io/github/issues/Alessandro-Salerno/Bialetti.svg?style=flat-square
[issues-url]: https://github.com/Alessandro-Salerno/Bialetti/issues
[license-shield]: https://img.shields.io/github/license/Alessandro-Salerno/Bialetti.svg?style=flat-square
[license-url]: https://github.com/Alessandro-Salerno/Bialetti/blob/master/LICENSE
