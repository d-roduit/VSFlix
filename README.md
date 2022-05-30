<div align="center">
            <a href="https://github.com/d-roduit/VSFlix"><img src="readme_pictures/VSFlix_Logo_wide.png" align="center" height="150" alt="VSFlix Logo"></a>

#

<p align="center">
    <strong>A JavaFX media streaming application bundled with its own server</strong>
</p>

</div>


<div align="center">
    <a href="https://github.com/d-roduit/VSFlix"><img src="readme_pictures/VSFlix_Screenshot.png" align="center" height="400" alt="Demo screenshot"></a>
</div>
<br>

The VSFlix project's goal is to be able to make one or multiple clients communicate with a single one server in order to retrieve files shared by the clients.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Technologies](#technologies)
3. [Guides and resources](#guides-and-resources)
4. [Authors](#authors)
5. [License](#license)

## <a name="getting-started"></a>Getting Started

### Running the server and the client application

There are two ways to run the project:

1. **Run the server and client JARs:** The first and fastest way is to simply download the server and client JARs from the [releases section](https://github.com/d-roduit/VSFlix/releases) and run them with the commands `java -jar server.jar` and `java -jar client.jar`.
2. **Run the project with your editor:**
    1. Download the project files to your computer and open the project in your favorite IDE (Eclipse, IntelliJ IDEA, etc.).
    2. Download the project dependencies with Maven. _(This step is often proposed or performed automatically by the IDE)_
    3. Run the server. The starting class for the server is `ch.dc.Main` in the server folder.
    
        _N. B. : The server listens any incoming connection on ip address `0.0.0.0` and uses by default port `50000`. If this port is already used by another process, you can change it directly in the starting class or by passing the port number as an argument when running the program._

    4. Run one (or more) client(s). The starting class for the client application is `ch.dc.Main` in the client folder.

Once you successfully ran the server and the client, you will be able to use the VSFlix client to stream (bidirectionnal stream) files from / to the other connected clients !

### Creating the JARs

#### In IntelliJ

In IntelliJ, you create JARs by creating what they call artifacts:

- Open `Project Structure` and go to `Artifacts`.
- Click on the `+` sign, then `JAR` > `From modules with dependencies`, then fill in the fields:
    - Select the module you want to create the JAR for and its main class
    - For `JAR files from libraries`, select `extract to the target JAR`.
    - For `Directory for META-INF/MANIFEST.MF`, the client and server JARs have differents paths:
        - For the client JAR, the path will automatically point to the resources folder (`[...]\src\main\resources`). If it is not, you can set it to point to the resources folder.
        - For the server JAR, the path will automatically point to the java source folder (`[...]\src\main\java`). Change it to point to the src folder (`[...]\src`).
- Click "OK" to finish the artifact configuration.
- In the menu bar, click `Build` > `Build artifacts...` > `All Artifacts` > `Build`.

Once you have done these steps, both the server and client JARs will have been created in the `out\artifacts` folder at root of the project !

#### In Eclipse

Before creating the JARs, make sure the build path of each module is correctly set up. See these [build path examples screenshots](build_paths_screenshots_for_creating_JARs_with_eclipse/) to see how the build paths should be configured. To configure the build path, right click on the module you want to configure the build path for and select `Build Path` > `Configure Build Path...`, then see the **"source"** tab.

Then, create the JAR with the following steps:

- In the Project Exporer, right click on the module you want to export, then select `Export`.
- In the export window, select `Java` > `Runnable JAR file`, then click "Next".
- In the Runnable JAR file Specification window, fill in the fields:
    - For `Launch configuration`, select the correct configuration (the dropdown list will be empty if you have never run the client module via the editor yet).
    - For `Export destination`, fill in the path of the to-be-created JAR (e.g. `[...]\Desktop\client.jar`). The path must end with a filename !
    - For `Library handling`, select `Extract required libraries into generated JAR`.
- Click "Finish".

Once you have done these steps, the JAR will have been created in the selected location !

## <a name="technologies"></a>Technologies

This project relies entirely on Java :

- The client application has been made with JavaFX
- The server and its internal HTTP server have been developed in pure Java

### Under the hood

The VSFlix client and server have there own way of communicating.
The communication is based on simple text commands that the client sends every time it needs to retrieve a particular information.

Possible commands :

| Command | Action |
| --------- | --------- | 
| HTTPPORT <http_port> | Send the client HTTP Server port to the server when the client connects. |
| GETALLFILES | Request all shared files from the server. |
| ADDFILE <FileEntry> | Send a FileEntry object for the server to add it to its shared files. |
| UNSHAREFILE <FileEntry> | Send a FileEntry object for the server to remove it from its shared files. |
| GETNBCONNECTEDCLIENTS | Request the number of connected clients. |
| DISCONNECT | Send a request to end the connection. It must be noted that the client does not wait on the server to close its own connection. |

## <a name="guides-and-resources"></a>Guides and resources

* [JavaFX](https://openjfx.io/) - Open source client application platform for desktop, mobile and embedded systems built on Java
* [FontAwesome](https://fontawesome.com/) - Icons library and toolkit
* [Maven](https://maven.apache.org/) -  Software project management and comprehension tool

## <a name="authors"></a>Authors

<table>
   <tbody>
      <tr>
         <td align="center">
            <a href="https://github.com/d-roduit">
            <img src="https://github.com/d-roduit.png?s=75" width="75"><br />
            Daniel Roduit
            </a>
         </td>
         <td align="center">
            <a href="https://gitlab.com/g.cathy">
            <img src="https://secure.gravatar.com/avatar/8249f413f33aff71168b6c34d4bffbc3?s=180&d=identicon" width="75"><br />
            Cathy Gay
            </a>
         </td>
      </tr>
   </tbody>
</table>

## <a name="license"></a>License

This project is licensed under the MIT License
