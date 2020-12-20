package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.util.Point3D;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * This class is a possible solution to the game.
 * The class consists of a graphical interface (MyFrame) and an algorithmic interface (Arena).
 * In this class we used a number of methods that aim to eat a maximum of Pokemons in less than 10 moves per second.
 */
public class Ex2 implements Runnable {

    private static MyFrame _win;
    private static Arena _ar;
    private long id;
    private int scenario;
    private static HashMap<Integer, Point3D> agp;
    private static int numOfPoks;
    private static int numOfAgs;


    public static void main(String[] args) {
        Ex2 start;
        if(args.length == 2) {
            start = new Ex2(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
        else{
            loginMenu login = new loginMenu();
            login.show();
            while(login.isOn) {
                System.out.print("");
            }
            start = new Ex2(login.id, login.scenario);
        }
        Thread client = new Thread(start);
        client.start();
    }

    /**
     * A constructor who receives the player's ID number and the number of level in which the player will want to play.
     * The constructor allows the game to be loaded via the command line.
     * The user can choose whether to enter the ID number and level number within the command line
     * or through the graphical window.
     * @param id - the player ID
     * @param scenario - the chosen level
     */
    public Ex2(long id, int scenario) {
        this.id = id;
        this.scenario = scenario;
    }

    /**
     *Loading the selected level from the server and running the game algorithmically and graphically.
     */
    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(scenario);
        directed_weighted_graph gg = init(game);
        game.login(id);
        game.startGame();
        gameClient.panelTimer p = new panelTimer(game);
        _win.myPanel.add(p);
        _win.setVisible(true);
        _win.setTitle("Itai&Liav Pokemons Game" + " " + "Level: " + scenario);
        int ind = 0;
        long dt = 100;
        while (game.isRunning()) {
            moveAgents(game, gg);
            try {
                if (ind % 1 == 0) {
                    _win.repaint();
                }
                Thread.sleep(dt);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String finalScore = game.toString();
        System.out.println(finalScore);
        System.exit(0);
    }

    /**
     * Gets the desired level from the server as a JSON file, reads the file and loads it into a graph.
     * Positions the Pokemon on the graph as called from the server.
     * Positions the agents correctly and cleverly on the graph,
     * each agent will be placed close to a different Pokemon and in different locations on the graph.
     * @param game - game_service
     * @return directed_weighted_graph that the game play on.
     */
    public directed_weighted_graph init(game_service game) {
        String g = game.getGraph();
        String gamePokemons = game.getPokemons();
        dw_graph_algorithms gra = new DWGraph_Algo();
        File output = new File("graph.txt");
        FileWriter writer;
        try {
            writer = new FileWriter(output);
            writer.write(g);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gra.load("graph.txt");
        directed_weighted_graph gg = gra.getGraph();
        openGUI(gg, gamePokemons);
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject server = line.getJSONObject("GameServer");
            int numOfAgents = server.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> poks = Arena.json2Pokemons(game.getPokemons());
            numOfPoks = poks.size();
            numOfAgs = numOfAgents;
            for (int i = 0; i < poks.size(); i++) {
                Arena.updateEdge(poks.get(i), gg);
            }
            agp = new HashMap<>();
            if (numOfAgs == 1 && numOfPoks == 2) {
                PriorityQueue<CL_Pokemon> pq = mostValuePok(poks);
                for (int i = 0; i < numOfAgents && !pq.isEmpty(); i++) {
                    CL_Pokemon pok = pq.poll();
                    edge_data e = pok.get_edge();
                    int dest;
                    if ((e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) && getRatio(e, gg, pok.getLocation()) > 0.15) {
                        dest = e.getSrc();
                    } else {
                        dest = e.getDest();
                    }
                    game.addAgent(dest);
                    agp.put(i, pok.getLocation());
                }
            } else {
                for (int i = 0; i < numOfAgents; i++) {
                    CL_Pokemon pok = choosePokemon(poks, gra);
                    edge_data e = pok.get_edge();
                    double rat = getRatio(e, gg, pok.getLocation());
                    int dest;
                    if ((e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) && getRatio(e, gg, pok.getLocation()) > 0.15) {
                        dest = e.getSrc();
                    } else {
                        dest = e.getDest();
                    }
                    agp.put(i, pok.getLocation());
                    game.addAgent(dest);
                    System.out.println(pok + " : edge - " + e + ", ratio: " + rat);
                    poks.remove(pok);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gg;
    }

    /**
     * Takes care of the updated graphic display of the game.
     * @param gg - directed_weighted_graph
     * @param gamePokemons - JSON format string represent all the pokemon
     */
    private static void openGUI(directed_weighted_graph gg, String gamePokemons) {
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(gamePokemons));
        _win = new MyFrame("Ex2", _ar);
        _win.setSize(1000, 700);
        _win.show();
    }

    /**
     * Moves the agents on the edges,
     * if the agent has reached to a node the method calls for an auxiliary method that
     * wisely selects the next edge on which the agent will advance.
     * @param game - game_service
     * @param gg - directed_weighted_graph
     */
    private void moveAgents(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        List<CL_Agent> agents = Arena.getAgents(lg, gg);
        _ar.setAgents(agents);
        String gamePokemons = game.getPokemons();
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(gamePokemons);
        _ar.setPokemons(pokemons);
        isAlive(agents, pokemons);
        for (int i = 0; i < agents.size(); i++) {
            CL_Agent ag = agents.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if (dest == -1) {
                dest = nextNode(gg, src, ag);
                game.chooseNextEdge(ag.getID(), dest);
                if (agp.get(ag.getID()) != null) {
                    if (_ar.getPokByPoint(agp.get(ag.getID())).get_edge().getWeight() < 0.3 && ag.getSpeed() > 2) {
                        for (int k = 0; k < 3; k++) {
                            game.move();
                        }
                    }
                }
                System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
            }
        }
    }

    /**
     * Checks whether a Pokemon that marked by an agent is still alive or has been eaten by another agent.
     * @param agents - List of agents
     * @param pokemons - List of Pokemons
     */
    private static void isAlive(List<CL_Agent> agents, List<CL_Pokemon> pokemons) {
        boolean flag;
        for (CL_Agent ag : agents) {
            flag = false;
            Point3D p = agp.get(ag.getID());
            if (p != null) {
                for (CL_Pokemon pok : pokemons) {
                    if (p.equals(pok.getLocation())) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    agp.replace(ag.getID(), null);
                }
            }
        }
    }

    /**
     * The method calls auxiliary functions according to the status of the agent
     * (its location, whether he chose a Pokemon he would like to eat, etc.)
     * and finally returns the edge that the agent needs to advance on.
     * @param g - directed_weighted_graph
     * @param src - this Agent node
     * @param agent - CL_Agent
     * @return
     */
    private static int nextNode(directed_weighted_graph g, int src, CL_Agent agent) {
        System.out.println(agp);

        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        int ans;

        if (agp.get(agent.getID()) != null) {
            System.out.println("try: i am on node " + g.getNode(src) + "value: " + agent.getValue());
            List<CL_Pokemon> lst = _ar.getPokemons();
            for (CL_Pokemon pok : lst) {
                Arena.updateEdge(pok, g);
            }
            CL_Pokemon myPok = _ar.getPokByPoint(agp.get(agent.getID()));
            System.out.println("my pok is: " + myPok.get_edge());

            edge_data whereICameFrom = myPok.get_edge();
            if (whereICameFrom.getDest() == src || whereICameFrom.getSrc() == src) {
                boolean flag = false;
                edge_data whereIGo = null;
                CL_Pokemon myNextPok = null;
                Collection<edge_data> c = g.getE(src);
                for (edge_data e : c) {
                    for (CL_Pokemon pok : lst) {
                        edge_data flipEdge = g.getEdge(e.getDest(), e.getSrc());
                        if ((pok.get_edge().equals(e) || pok.get_edge().equals(flipEdge)) && pok != myPok && !pok.get_edge().equals(whereICameFrom)) {
                            flag = true;
                            whereIGo = pok.get_edge();
                            myNextPok = pok;
                        }
                    }
                }
                if (flag) {
                    System.out.println("agent " + agent.getID() + " on node " + agent.getSrcNode()
                            + "I came from edge " + whereICameFrom + " and i continue to edge " + whereIGo);
                    System.out.println("i came for " + myPok + " and i continue to " + myNextPok);
                    agp.replace(agent.getID(), myNextPok.getLocation());
                    System.out.println(whereICameFrom.getDest() == src);
                    System.out.println(whereIGo.getSrc() == src);
                    int sorc = whereIGo.getSrc();
                    if (src == sorc) {
                        return whereIGo.getDest();
                    } else {
                        return whereIGo.getSrc();
                    }
                }
            }
        }

        if (agp.get(agent.getID()) == null) {
            System.out.println("no");
            List<node_data> lst = nextPokemonByDis(ga, src, agent);
            if (lst.isEmpty()) {
                int dest = ga.getGraph().getE(agent.getSrcNode()).iterator().next().getDest();
                ans = dest;
            } else {
                ans = lst.remove(0).getKey();
            }
        } else {
            System.out.println("OHno");
            CL_Pokemon current = _ar.getPokByPoint(agp.get(agent.getID()));
            List<node_data> lst = continueToPok(ga, src, agent, current);
            if (lst.isEmpty()) {
                int dest = ga.getGraph().getE(agent.getSrcNode()).iterator().next().getDest();
                ans = dest;
            } else {
                ans = lst.remove(0).getKey();
            }
        }
        return ans;
    }

    /**
     * The method selects for the agent the Pokemon that will best pay for him to eat.
     * The method calls "goodList" method that returns a shortlist of Pokemon available for eating for the current agent.
     * @param g - dw_graph_algorithms
     * @param src - this agent node
     * @param agent - CL_Agent
     * @return a List of node represent a path to the chosen Pokemon
     */
    private static synchronized List<node_data> nextPokemonByDis(dw_graph_algorithms g, int src, CL_Agent agent) {
        List<CL_Pokemon> pokemons = _ar.getPokemons();
        for (CL_Pokemon pok : pokemons) {
            Arena.updateEdge(pok, g.getGraph());
        }
        List<CL_Pokemon> yourPoks = new LinkedList<>();
        Collection<Point3D> c = agp.values();
        boolean flag;
        for (CL_Pokemon pok : pokemons) {
            flag = false;
            for (Point3D p : c) {
                if (p != null) {
                    if (p.equals(pok.getLocation())) {
                        flag = true;
                    }
                }
            }
            if (!flag) {
                yourPoks.add(pok);
            }
        }
        List<CL_Pokemon> yourFinalPoks = goodList(yourPoks, g, agent);
        if (yourFinalPoks.isEmpty()) {
            List<node_data> emptyList = new LinkedList<>();
            return emptyList;
        }
        Iterator<CL_Pokemon> pokemonsItr = yourFinalPoks.listIterator();
        CL_Pokemon closer = null;
        double min = Double.MAX_VALUE;
        List<node_data> lst;
        List<node_data> finalLst = null;
        while (pokemonsItr.hasNext()) {
            CL_Pokemon currentPok = pokemonsItr.next();
            edge_data currentPokEdge = currentPok.get_edge();
            int type = currentPok.getType();
            double d;
            if (currentPokEdge.getDest() > currentPokEdge.getSrc() && type == 1 || currentPokEdge.getDest() < currentPokEdge.getSrc() && type == -1) {
                d = g.shortestPathDist(src, currentPokEdge.getSrc()) + currentPokEdge.getWeight();
                lst = g.shortestPath(src, currentPokEdge.getSrc());
                lst.add(g.getGraph().getNode(currentPokEdge.getDest()));
            } else {
                d = g.shortestPathDist(src, currentPokEdge.getDest()) + currentPokEdge.getWeight();
                lst = g.shortestPath(src, currentPokEdge.getDest());
                lst.add(g.getGraph().getNode(currentPokEdge.getSrc()));
            }
            lst.remove(0);
            if (d < min) {
                min = d;
                closer = currentPok;
                finalLst = lst;
            }
        }
        System.out.println(agent.getID() + " pick: " + closer);
        agp.replace(agent.getID(), closer.getLocation());
        return finalLst;
    }

    /**
     * If an agent chooses a Pokemon that he wants to eat,
     * the method will check whether it pays to continue advancing towards the chosen Pokemon
     * or to choose another Pokemon .
     * (when Pokemon is eaten a new Pokemon is created in a random place on the graph
     * so it may be better to choose another Pokemon to advance towards).
     * @param g - dw_graph_algorithms
     * @param src - this Agent nodes' id
     * @param agent - CL_Agent
     * @param myPokemon - CL_Pokemon
     * @return a List of node represent a path to the chosen Pokemon
     */
    private static List<node_data> continueToPok(dw_graph_algorithms g, int src, CL_Agent agent, CL_Pokemon
            myPokemon) {
        double disToMaybeBetterPok = Double.MAX_VALUE;
        List<node_data> maybeBetterPok = new LinkedList<>();
        if (numOfPoks > numOfAgs) {
            maybeBetterPok = nextPokemonByDis(g, src, agent);
            if (!maybeBetterPok.isEmpty()) {
                disToMaybeBetterPok = 0;
                for (int i = 0; i < maybeBetterPok.size() - 1; i++) {
                    node_data sorc = maybeBetterPok.get(i);
                    node_data dest = maybeBetterPok.get(i + 1);
                    disToMaybeBetterPok += g.getGraph().getEdge(sorc.getKey(), dest.getKey()).getWeight();
                }
            }
        }
        if (numOfAgs == numOfPoks) {
            List<CL_Pokemon> p = new LinkedList<>();
            p.add(myPokemon);
            List<CL_Pokemon> goodList = goodList(p, g, agent);
            if (goodList.isEmpty()) {
                agp.replace(agent.getID(), null);
                return maybeBetterPok;  //return empty list
            }
        }
        Arena.updateEdge(myPokemon, g.getGraph());
        List<node_data> lst;
        edge_data e = myPokemon.get_edge();
        double disToMyPok;
        int type = myPokemon.getType();
        if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
            disToMyPok = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
            lst = g.shortestPath(src, e.getSrc());
            lst.add(g.getGraph().getNode(e.getDest()));
        } else {
            disToMyPok = g.shortestPathDist(src, e.getDest()) + e.getWeight();
            lst = g.shortestPath(src, e.getDest());
            lst.add(g.getGraph().getNode(e.getSrc()));
        }
        lst.remove(0);
        if (disToMaybeBetterPok < disToMyPok) {
            lst = maybeBetterPok;

        } else {
            agp.replace(agent.getID(), myPokemon.getLocation());
            System.out.println(agent.getID() + " countinue to: " + myPokemon);
        }
        return lst;
    }

    /**
     * this method is used in init method.
     * The method returns a priority queue of Pokemon sorted by their values.
     * @param poks The list of Pokemon from which we want to select a Pokemon.
     * @return List of Pokemon sorted by priority queue by their value.
     */
    public PriorityQueue<CL_Pokemon>
    public PriorityQueue<CL_Pokemon> mostValuePok(ArrayList<CL_Pokemon> poks) {
        PriorityQueue<CL_Pokemon> pq = new PriorityQueue<>(poks.size(), (o1, o2) -> {
            if (o1.getValue() < o2.getValue()) {
                return 1;
            } else if (o1.getValue() > o2.getValue()) {
                return -1;
            } else {
                return 0;
            }
        });
        pq.addAll(poks);
        return pq;
    }

    /**
     * A private method that gets a list of all the Pokemon on the graph.
     * The method filters Pokemon that are closer to other agents and returns a shortlist of Pokemon
     * available for eating for the current agent.
     * @param currentList the current Pokemon list.
     * @param g the graph we use.
     * @param currentAgent the agent who must choose the Pokemon.
     * @return the final list from which Pokemon was selected.
     */
    public static List<CL_Pokemon> goodList(List<CL_Pokemon> currentList, dw_graph_algorithms g, CL_Agent
            currentAgent) {
        List<CL_Pokemon> lst = currentList;
        Iterator<CL_Pokemon> currentListItr = lst.listIterator();
        Iterator<CL_Agent> agentsItr = _ar.getAgents().listIterator();
        List<CL_Pokemon> finalPoks = new LinkedList<>();
        boolean flag;
        while (currentListItr.hasNext()) {
            flag = true;
            CL_Pokemon currentPok = currentListItr.next();
            int currentPokType = currentPok.getType();
            edge_data currentPokEdge = currentPok.get_edge();
            double disFromCurrentAgentToPok;
            if (currentPokEdge.getDest() > currentPokEdge.getSrc() && currentPokType == 1 || currentPokEdge.getDest() < currentPokEdge.getSrc() && currentPokType == -1) {
                disFromCurrentAgentToPok = g.shortestPathDist(currentAgent.getSrcNode(), currentPok.get_edge().getSrc()) + currentPokEdge.getWeight();
            } else {
                disFromCurrentAgentToPok = g.shortestPathDist(currentAgent.getSrcNode(), currentPok.get_edge().getDest()) + currentPokEdge.getWeight();
            }
            while (agentsItr.hasNext()) {
                CL_Agent otherAgent = agentsItr.next();
                if (otherAgent != currentAgent) {
                    double disFromOtherAgentToPok;
                    if (agp.get(otherAgent.getID()) != null) {
                        CL_Pokemon hisPok = _ar.getPokByPoint(agp.get(otherAgent.getID()));
                        int hisPokType = hisPok.getType();
                        edge_data hisPokEdge = hisPok.get_edge();
                        if (hisPokEdge.getDest() > hisPokEdge.getSrc() && hisPokType == 1 || hisPokEdge.getDest() < hisPokEdge.getSrc() && hisPokType == -1) {
                            if (currentPokEdge.getDest() > currentPokEdge.getSrc() && currentPokType == 1 || currentPokEdge.getDest() < currentPokEdge.getSrc() && currentPokType == -1) {
                                disFromOtherAgentToPok = g.shortestPathDist(hisPok.get_edge().getDest(), currentPok.get_edge().getSrc()) + currentPokEdge.getWeight();
                            } else {
                                disFromOtherAgentToPok = g.shortestPathDist(hisPok.get_edge().getDest(), currentPok.get_edge().getDest()) + currentPokEdge.getWeight();

                            }
                        } else {
                            if (currentPokEdge.getDest() > currentPokEdge.getSrc() && currentPokType == 1 || currentPokEdge.getDest() < currentPokEdge.getSrc() && currentPokType == -1) {
                                disFromOtherAgentToPok = g.shortestPathDist(hisPok.get_edge().getSrc(), currentPok.get_edge().getSrc()) + currentPokEdge.getWeight();
                            } else {
                                disFromOtherAgentToPok = g.shortestPathDist(hisPok.get_edge().getSrc(), currentPok.get_edge().getDest()) + currentPokEdge.getWeight();
                            }
                        }
                    } else {
                        if (currentPokEdge.getDest() > currentPokEdge.getSrc() && currentPokType == 1 || currentPokEdge.getDest() < currentPokEdge.getSrc() && currentPokType == -1) {
                            disFromOtherAgentToPok = g.shortestPathDist(otherAgent.getSrcNode(), currentPok.get_edge().getSrc()) + currentPokEdge.getWeight();
                        } else {
                            disFromOtherAgentToPok = g.shortestPathDist(otherAgent.getSrcNode(), currentPok.get_edge().getDest()) + currentPokEdge.getWeight();
                        }
                    }
                    if (disFromOtherAgentToPok <= disFromCurrentAgentToPok) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                finalPoks.add(currentPok);
            }
            agentsItr = _ar.getAgents().listIterator();
        }
        return finalPoks;
    }

    /**
     * The method returns the most lucrative Pokemon to start the game next to.
     * The method checks which Pokemon has the most Pokemons closest to it and so the agent
     * will start the game in the best position for him.
     * @param pokList a list of Pokemon from which you can select a Pokemon.
     * @param g the graph that we use.
     * @return the best Pokemon to go to.
     */
    private static CL_Pokemon choosePokemon(List<CL_Pokemon> pokList, dw_graph_algorithms g) {
        Iterator<CL_Pokemon> poks = pokList.listIterator();
        Iterator<CL_Pokemon> poks2 = pokList.listIterator();
        CL_Pokemon chosen = null;
        double d = 0;
        double d1 = 1;
        while (poks.hasNext()) {
            CL_Pokemon currentPok = poks.next();
            edge_data currentPokEdge = currentPok.get_edge();
            int key1;
            if ((currentPokEdge.getDest() > currentPokEdge.getSrc() && currentPok.getType() == 1 ||
                    currentPokEdge.getDest() < currentPokEdge.getSrc() && currentPok.getType() == -1) &&
                    getRatio(currentPokEdge, g.getGraph(), currentPok.getLocation()) > 0.15) {
                key1 = currentPokEdge.getSrc();
            } else {
                key1 = currentPokEdge.getDest();
            }
            while (poks2.hasNext()) {
                CL_Pokemon otherPok = poks2.next();
                if (currentPok != otherPok) {
                    edge_data otherPokEdge = otherPok.get_edge();
                    int key2;
                    if (otherPokEdge == currentPokEdge) {
                        key2 = key1;
                    } else if ((otherPokEdge.getDest() > otherPokEdge.getSrc() && otherPok.getType() == 1 ||
                            otherPokEdge.getDest() < otherPokEdge.getSrc() && otherPok.getType() == -1) &&
                            getRatio(currentPokEdge, g.getGraph(), currentPok.getLocation()) > 0.15) {
                        key2 = currentPokEdge.getSrc();
                    } else {
                        key2 = currentPokEdge.getDest();
                    }
                    d1 += g.shortestPathDist(key1, key2);
                }
            }
            if (currentPok.getValue() / d1 > d) {//&& getRatio(e,g.getGraph(),pok.getLocation()) > 0.15) {
                chosen = currentPok;
                d = currentPok.getValue() / d1;
            }
            d1 = 1;
            poks2 = pokList.listIterator();
        }
        return chosen;
    }


    /**
     * Returns the relative position of a point on an edge.
     * @param e the edge of the pokemon.
     * @param g the graph we use.
     * @param point the point of the pokemon.
     * @return the ratio of the point on the edge.
     */
    public static double getRatio(edge_data e, directed_weighted_graph g, Point3D point) {
        if (e != null) {
            int src = e.getSrc();
            int dest = e.getDest();
            double ratio = g.getNode(src).getLocation().distance(point) / g.getNode(src).getLocation().distance(g.getNode(dest).getLocation());
            return ratio;
        }
        return -1;
    }
}
