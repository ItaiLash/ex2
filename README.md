![ArielLogo](docs/ArielLogo.png)
# Assignment 2

> Made by Itai Lashover and Liav weiss
>
> GitHub pages:  
> [https://github.com/ItaiLash](https://github.com/ItaiLash)  
> [https://github.com/liavweiss](https://github.com/liavweiss)



### Introduction
This project is an assignment in an object-oriented course at Ariel University.
The project consists of two parts:
The first part is an implenentation of directed weighted graph and consist 14 classes, 6 interfaces with 7 implementations
and another class belonging to the second part of the assignment that we will detail below.
The second part is a simple game for catching pokemons on directed weighted graph using appropriate algorithms.
this part cosist 4 simple classes for represente a point and range on a graph, 3 classes which represent the game algorithmically,
4 GUI classes which represent the display of the game graphically and main class (called "Ex2") that play the game.

# First part (api package)

## geoLocation class - implemets eo_location
geoLocation class ia an implementation of geo_location interface.
This class represents a geo location (x,y,z), aka Point3D.

| **Methods**      |    **Details**        |
|-----------------|-----------------------|
| `geoLocation()` | 2 diffrent constructors of a new geoLocation |
| `distance(geo_location g)` | Returns the distance between two points |
| `equals(Object o)` | comparing all the fields between the two geoLocations |


## node class - implements node_data
node is an implementation of a node_info interface.
This class is a simple class that represents a vertex on a directed weighted graph and implement Set of simple operations.

Each node contains few fields:
* location: An object that represent the location of the node by 3d point.
* weight: A variable that is used in later functions, by default Initialized to Integer.MAX_VALUE(infinite).
* info: A variable that is used in later functions, by default Initialized to "White".
* tag: A variable that is used in later functions, by default Initialized to -1.
* key: A unique key that is used as each node's ID.

| **Methods**      |    **Details**        |
|-----------------|-----------------------|
| `node()` | 3 diffrent constructors of a new node with unique key |
| `getKey()` | Returns the key (id) associated with this node |
| `getLocation()` | Returns the location of this node |
| `setLocation(geo_location p)` | Allows changing this node's location |
| `getWeight()` | Returns the weight associated with this node |
| `setWeight(double w)` | Allows changing this node's weight |
| `getInfo()` | Returns the remark associated with this node |
| `setInfo(String s)` | Allows changing the remark associated with this node |
| `getTag()` | Returns the tag associated with this node |
| `setTag(int t)` | Allows setting the tag value for temporal marking an node |
| `compareTo(node_data o)` | Override on compareTo in order to compare two nodes just by their weight |
| `equals(Object o)` | Comparing all the fields of the two nodes |

## edge class - implements edge_data
edge class is an implementation of edge_data interface.
This class implement a set of operations applicable on a directional edge(src --> dest) in a (directional) weighted graph.

Each edge contains few fields:
* src: A variable that represent the id of the source node of this edge.
* dest: A variable that represent the id of the destination node of this edge.
* w: A variable represent this edge weight (positive value).
* info: A variable represent this edge remark (meta data).
* tag: A variable represent temporal data.

| **Methods**      |    **Details**        |
|-----------------|-----------------------|
| `edge()` | 3 diffrent constructors of a new directed edge |
| `getSrc()` | Returns the id of the source node of this edge |
| `getDest()` | Returns the id of the destination node of this edge |
| `getWeight()` | Returns the weight of this edge (positive value) |
| `getInfo()` | Returns the remark associated with this edge |
| `setInfo(String s)` | Allows changing the remark associated with this edge |
| `getTag()` | Returns the tag associated with this edge |
| `setTag(int t)` | Allows setting the tag value for temporal marking an edge |
| `equals(Object o)` | Comparing all the fields of the two edges |


## DWGraph_DS class - implements directed_weighted_graph

DWGraph_DS class is an implementation of directed_weighted_graph interface.
This class implement an directional weighted graph.
It support a large number of nodes (over 100,000).
This implementation based on HashMap data structure.

Each DWGraph_DS contains few fields:
* nodes: HashMap data structure that represents the group of nodes of this graph.
* edges: HashMap data structure that represents each node group of directed edges in this graph.
* numOfNode: A variable that stored the amount of nodes in this graph.
* numOfEdge: A variable that stored the amount of edges in this graph.
* mc: Mode Count a variable that stored the amount of changes
* (add node, remove node, add edge, remove edge) made in this graph.

| **Main methods**      |    **Details**        |    **Complexity**   |
|-----------------|-----------------------|-----------------------|
| `DWGraph_DS()` | Default constructor | O(1) |
| `DWGraph_DS(directed_weighted_graph other)` | Deep copy constructor ,it calls another private methods that initialize his HashMaps | O(n^2) n=V.degree |
| `getNode(int key)` | Returns the node_data by the node id | O(1) |
| `getEdge(int src, int dest)` | Returns the edge (src,dest) between two nodes | O(1) |
| `addNode(node_data n)` | Adds a new node to the graph | O(1) |
| `connect(int src, int dest, double w)` | Connects a directed weighted edge between node src to node dest | O(1) |
| `getV()` | Returns a collection representing all the nodes in the graph | O(1) |
| `getE(int node_id)` | Returns a collection representing all the edges getting out of the given node | O(1) |
| `removeNode(int key)` | Deletes a node from the graph and removes all edges which starts or ends at this node | O(k) k=V.degree |
| `removeEdge(int src, int dest)` | Deletes an edge from the graph | O(1) |
| `nodeSize()` | Returns the number of nodes in the graph | O(1) |
| `edgeSize()` | Returns the number of edges in the graph | O(1) |
| `getMC()` | Returns the Mode Count | O(1) |
| `equals(Object o)` | Comparing all the fields of the object, it calls another private methods that compare the two HashMaps |  |


###### private methods
* `nodesDeepCopy(directed_weighted_graph other, HashMap nodes)` : private method gets a graph and empty HashMap and initialize the HashMap to be a duplicate of his HashMap.
* `edgeDeepCopy(directed_weighted_graph other, HashMap edges)` : private method gets a graph and empty HashMap and adds to this HashMap the same directed weighted edges.
* `graphNodeEquals(HashMap<Integer, node_data> other)` : private method returns true if the two HashMaps are equal to each other and false otherwise.
* `graphEdgesEquals(HashMap<Integer, HashMap<Integer,edge_data>> other)` : private method returns true if the two HashMaps(in HashMaps)
are equal to each other and false otherwise.


## DWGraph_Algo class - implenents dw_graph_algorithms
DWGraph_Algo class is an implementation of dw_graph_algorithms interface.
This class represents a directed (positive) weighted Graph and implement Theory Algorithms including:
clone, init, isConnected, shortedPath and save&load with JSON file.

| **Main methods**      |    **Details**        |    **Complexity**   |
|-----------------|-----------------------|-----------------------|
| `DWGraph_Algo()` | Default constructor | O(1) |
| `init(directed_weighted_graph g)` | Init the graph on which this set of algorithms operates | O(1) |
| `getGraph()` | Return the underlying graph of which this class works | O(1) |
| `copy()` | Compute a deep copy of this weighted graph | O(n^2) n=V.degree) |
| `isConnected()` | Check whether the graph is strongly connected (there is a valid path from each node to each other node) | O(n*(n+k)) n=V.degree, k=E.degree |
| `shortestPathDist(int src, int dest)` | Returns the length of the shortest path between src to dest | O((n+k)log(n)) n=V.degree, k=E.degree |
| `shortestPath(int src, int dest)` | Returns the shortest path between src to dest - as an ordered List of nodes | O((n+k)log(n)) n=V.degree, k=E.degree |
| `save(String file)` | Saves this weighted directed graph to the given file name - in JSON format | |
| `load(String file)` | Load a graph to this graph algorithm - from JSON format | |
| `equals(Object o)` | Comparing all the fields of the object |  |



###### private methods
* `bfs(weighted_graph g)` : This private method based on breadth-first search.
BFS is an algorithm for traversing or searching graph data structures.
The method checks whether or not the graph is strongly linked,
in other words it checks whether there is a path between node to each other node.
The method use counter that count the number of nodes that connected to the source node.
If counter value equal to the number of nodes in this graph that means that the source node connected.
To check if the whole graph is strongly connected needs to run the method on all the nodes in the graph.
The method stored a queue of the visited nodes:
1. Pop the first node from the queue.
2. Gets a collection of this node edges.
3. Goes through all the nodes that have an edge from the pop node.
4. Check if the node has already been visited, if so skip it(tag = 1 -> visited, tag = -1 -> not visited).
  Otherwise mark it as visited (update his own tag) and add the node to the queue.
5. Add this node's neighbors to the queue and repeat these steps
The method use counter that count the number of nodes that connected to the source node.
After the queue is empty check if the counter value equal to the number of nodes in this graph
that means that the source node connected.
If so the method will return true, Otherwise false.
Note: The method change the tag values.
Complexity: O(|V|+|E|), |V|=number of nodes, |E|=number of edges.

* `Dijkstra(node_info src, node_info dest)` : This private method based on Dijkstra's algorithm.
Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph.
In other words it finds the shortest paths between the source node and the destination node.
The method uses the weight of each node to update his current distance from the source node.
The method stored a priority queue(priority is determined by the weight) of the visited nodes:
1. Pop the first node from the queue.
2. Visit each one of this nodes neighbors:
3. Check if the node has already been visited, if so skip it(tag = Black -> visited, tag = White -> not visited).
4. Updates his weight to be the distance between the node and the source node.
5. Updates his tag To be the node's id from which he came to.
6. Add this node to the queue.
7. After going through all the neighbors of the node, updates that we visited this node by change his info to "Black" and therefore will not visit it again.
8. Repeat these steps until the queue is empty or has reached the destination node.
If the queue is empty it means it did not reach the destination node (the graph is not connected), return infinity.
Otherwise returns the tag of the destination node
Note: The method change the info, tag and pre values.
Complexity: O((|V|+|E|)log|V|), |V|=number of nodes, |E|=number of edges.

* `resetTag` : private method resets the values of all the tags of the nodes in the graph.
  Reset the value = change it back to default value: -1.
* `resetWeight` : private method resets the value of weight in each node in the graph.
  Reset the value = change it back to default value: Double.MAX_VALUE (infinity).
* `resetInfo` : private method resets the value of info in each node in the graph.
  Reset the value = change it back to default value: "White".
  
  
# How to use?
Create main class and run the code below (for example):
```
public static void main(String[] args) {  

     directed_weighted_graph g = new DWGraph_DS();

        node_data n0 = new node(new geoLocation(0, 0, 0));   //0
        node_data n1 = new node(new geoLocation(0, 0, 0));   //1
        node_data n2 = new node(new geoLocation(1, -1, 0));  //2
        node_data n3 = new node(new geoLocation(2, 2, 0)); //3
        node_data n4 = new node(new geoLocation(3, -0.5, 0));   //4
        node_data n5 = new node(new geoLocation(4, -0.8, 0));   //5
        node_data n6 = new node(new geoLocation(8, -4, 0));   //6
        node_data n7 = new node(new geoLocation(8, 0, 0));   //7
        node_data n8 = new node(new geoLocation(4.5, 2, 0));   //8

        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        g.addNode(n7);
        g.addNode(n8);
        
        g.connect(1, 3, 5);
        g.connect(1, 2, 9);
        g.connect(2, 4, 18);
        g.connect(2, 1, 3);
        g.connect(3, 4, 12);
        g.connect(4, 8, 8);
        g.connect(4, 2, 2);
        g.connect(5, 4, 9);
        g.connect(5, 7, 5);
        g.connect(5, 8, 3);
        g.connect(5, 6, 2);
        g.connect(6, 7, 1);
        g.connect(7, 5, 4);
        g.connect(7, 8, 6);
        g.connect(8, 5, 3);

        
        directed_weighted_graph g2 = new DWGraph_DS();
        //g2.addNode(n0);


        System.out.println(g);
        dw_graph_algorithms gra = new DWGraph_Algo();
        gra.init(g);
        System.out.println(gra.isConnected());
        System.out.println(gra.shortestPathDist(0, 3));
        System.out.println(gra.shortestPath(0, 3));
        //gra.save("theGraph.obj");
        gra.load("theGraph.obj");
        System.out.println(gra.getGraph());
     }
```
The code creates the graph:

<img src="docs/directed weighted graph.png" width="500">

The output will be:
```
DWGraph_DS{nodes={[1][2][3][4][5][6][7][8]}, edges={{ 1--9.0-->2 1--5.0-->3 }{ 2--3.0-->1 2--18.0-->4 }{ 3--12.0-->4 }
{ 4--2.0-->2 4--8.0-->8 }{ 5--9.0-->4 5--2.0-->6 5--5.0-->7 5--3.0-->8 }{ 6--1.0-->7 }{ 7--4.0-->5 7--6.0-->8 }{ 8--3.0-->5 }}}
true
30.0
[[1], [3], [4], [8], [5], [6]]
```
## External info:
- More about graph : https://en.wikipedia.org/wiki/Graph_%28discrete_mathematics%29
- More about Dijkstra's algorithm : https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
- More about BFS algorithm : https://en.wikipedia.org/wiki/Breadth-first_search
