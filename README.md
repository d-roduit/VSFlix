# VSFlix

<div align="center">
            <a href="https://gitlab.com/d-roduit/vsflix/"><img src="client/src/main/resources/ch/dc/assets/images/vsflix.png" align="center" height="150" alt="VSFlix Logo"></a>

#

<p align="center">
    <strong>A JavaFX media streaming application bundled with its own server</strong>
</p>

</div>


<div align="center">
    <a href="https://gitlab.com/d-roduit/vsflix/"><img src="VSFlix_Screenshot.png" align="center" height="400" alt="Demo screenshot"></a>
</div>
<br>


The VSFlix project's goal is to be able to make one or multiple clients communicate with a single one server in order to retrieve files shared by the clients.

The technology used for this project rely entirely on Java (JavaFX application for the GUI and Java-based server and Http Server).


## Table of Contents

1. [Getting Started](#getting-started)
2. [Running the tests](#running-the-tests)
3. [Authors](#authors)
4. [Guides and resources](#guides-and-resources)
5. [License](#license)

## <a name="getting-started"></a>Getting Started

### Installing

You will need to follow the steps below in order to launch the project.

1. Unzip the file downloaded.
2. Launch the VSFlix server by double-clicking on the server jar.
3. Launch the VSFlix client application by double-clicking on the client jar.

Once these three steps have been executed, you will be able to use the VSFlix client to stream (bidirectionnal stream) files from / to the other connected clients.

### Under the hood

The VSFlix client and server have there own way of communicating.
The communication is based on simple text commands that the client sends every time it needs to retrieve a particular information.

Possible commands :

- **HTTPPORT <http_port>** : Send the client HTTP Server port to the server when the client connects.
- **GETALLFILES** : Request all shared files from the server.
- **ADDFILE <FileEntry>** : Send a FileEntry object for the server to add it to its shared files.
- **UNSHAREFILE <FileEntry>** : Send a FileEntry object for the server to remove it from its shared files.
- **GETNBCONNECTEDCLIENTS** : Request the number of connected clients.
- **DISCONNECT** : Send a request to end the connection. It must be noted that the client does not wait on the server to close its own connection.

## <a name="authors"></a>Authors

<table>
   <tbody>
      <tr>
         <td align="center" valign="top" width="11%">
            <a href="https://github.com/d-roduit">
            <img src="https://github.com/d-roduit.png?s=75" width="75" height="75"><br />
            Daniel Roduit
            </a>
         </td>
         <td align="center" valign="top" width="11%">
            <a href="https://gitlab.com/g.cathy">
            <img src="https://secure.gravatar.com/avatar/8249f413f33aff71168b6c34d4bffbc3?s=180&d=identicon" width="75" height="75"><br />
            Cathy Gay
            </a>
         </td>
      </tr>
   </tbody>
</table>

## <a name="guides-and-resources"></a>Guides and resources

* [JavaFX](https://openjfx.io/) - JavaFX
* [FontAwesme](https://fontawesome.com/) - FontAwesome 5 Icons
* [Maven](https://maven.apache.org/) - Dependency Management


## <a name="license"></a>License

This project is licensed under the MIT License
