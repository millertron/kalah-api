# Kalah REST API
This is a Java (11) project implementation of the Kalah game using Spring Boot framework and Maven.

## Running the code ##
To run the application via an IDE (this was developed in IntelliJ), simply import the maven project and run the main application: KalahApiApplication.java

Otherwise, the executable jar file should be found in target -> kalah-api-0.0.1-SNAPSHOT.jar

and this should be runnable using Java command line.

``java -jar kalah-api-0.0.1-SNAPSHOT.jar``

The application should start on http://localhost:8080 by default.

## Available endpoints ##

|Method|Path|Description|Response Code|Response Body|
|---|---|---|---|---|
|POST|/games|Creates a new game|201|id: ID of game<br/>url: requested URL|
|PUT|/games/{gameId}/pits/{pitId}|Make a move|200<br/>404<br/>400<br/>|id: ID of game<br/>url: requested URL<br/>status: map representation of game's pits|

A basic outline of the API is available to download in yaml format at http://localhost:8080/v3/api-docs.yaml

## Key features ##
- Kalah game is initialized with 7x7 size pits with 6 stones at each non-kalah pits.
- Player can move a stone to their own kalah pit but will skip the opponent's kalah pit.
- Player will take all the stones from the pit opposite to the last pit a stone was added if was empty and non-kalah.
- Once all non-kalah pits of either player's side are empty, the remaining stones in non-kalah pits are collected to the appropriate kalah pit.

### Missing implementations ###
- Validation to request for players playing out of turn.
- Turn tracking.
- Bonus round for the player putting a stone in their kalah pit.
- Endgame notification.

I didn't include them as they seemed to be out of scope of the assignment.
However, it was a rather fun exercise - I may add them at a later date and
even extend this with a front-end!
