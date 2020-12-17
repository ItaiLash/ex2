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


public class Ex2 implements Runnable {
    private static MyFrame2 _win;
    private static Arena _ar;
    private long id;
    private int scenario;
    private static HashMap<Integer, Point3D> agp;
    private static int numOfPoks;
    private static int numOfAgs;


    public static void main(String[] args) {
        loginMenu login = new loginMenu();
        login.chose();
        while (login.isOn) {
            System.out.print("");
        }
        Ex2 start = new Ex2(login.id, login.scenario);
        Thread client = new Thread(start);
        client.start();
    }

    /*
    public static void main(String[] args) {
        Ex22 start;
        if(args.length == 2) {
            start = new Ex22(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
        else{
            loginMenu login = new loginMenu();
            login.chose();
            while(login.isOn) {
                System.out.print("");
            }
            start = new Ex22(login.id, login.scenario);
        }
        Thread client = new Thread(start);
        client.start();
    }
*/
    public Ex2(long id, int scenario) {
        this.id = id;
        this.scenario = scenario;
    }


    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(scenario);
        directed_weighted_graph gg = init(game);
        game.login(id);
        game.startGame();
        gameClient.panelTimer p = new panelTimer(game);
        _win.myPanel.add(p);
        _win.setVisible(true);
        _win.setTitle("Itai&Liav Pokemons Game");
        int ind = 0;
        long dt = 0;
        while (game.isRunning()) {
            moveAgents(game, gg);
            try {
                if (ind % 1 == 0) {
                    _win.repaint();
                }
                //Thread.sleep(dt);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }

    public directed_weighted_graph init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
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
        directed_weighted_graph gg = gra.getGraph();        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame2("Ex2", _ar);
        _win.setSize(1000, 700);
        // _win.update(_ar);
        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int numOfAgents = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> poks = Arena.json2Pokemons(game.getPokemons());
            numOfPoks = poks.size();
            numOfAgs = numOfAgents;
            for (int i = 0; i < poks.size(); i++) {
                Arena.updateEdge(poks.get(i), gg);
            }
            agp = new HashMap<>();
            if (numOfAgs == 1 && numOfPoks > 2) {
                CL_Pokemon pok = choosePokemon1(poks, gra);
                edge_data e = pok.get_edge();
                int dest;
                if (e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) {
                    dest = e.getSrc();
                } else {
                    dest = e.getDest();
                }
                game.addAgent(dest);
                //agp.put(0, pok.getLocation());
            } else if (numOfAgs == 1 && numOfPoks == 2) {
                PriorityQueue<CL_Pokemon> pq = mostValuePok(poks);
                for (int i = 0; i < numOfAgents && !pq.isEmpty(); i++) {
                    CL_Pokemon pok = pq.poll();
                    edge_data e = pok.get_edge();
                    int dest;
                    if (e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) {
                        dest = e.getSrc();
                    } else {
                        dest = e.getDest();
                    }
                    game.addAgent(dest);
                    agp.put(i, pok.getLocation());
                }
            } else {
                for (int i = 0; i < numOfAgents; i++) {
                    CL_Pokemon pok = choosePokemon1(poks, gra);
                    edge_data e = pok.get_edge();
                    double rat = getRatio(e, gg, pok.getLocation());
                    int dest;
                    if (e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) {
                        dest = e.getSrc();
                    } else {
                        dest = e.getDest();
                    }
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

    private void moveAgents(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        //System.out.println(lg);
        List<CL_Agent> log = Arena.getAgents(lg, gg);
        _ar.setAgents(log);
        String fs = game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        boolean flag;
        for (CL_Agent ag : log) {
            flag = false;
            Point3D p = agp.get(ag.getID());
            if (p != null) {
                for (CL_Pokemon pok : ffs) {
                    if (p.equals(pok.getLocation())) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    agp.replace(ag.getID(), null);
                }
            }
        }
        for (int i = 0; i < log.size(); i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if (dest == -1) {
                dest = nextNode(gg, src, ag);
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
            }
        }
    }

    private static int nextNode(directed_weighted_graph g, int src, CL_Agent agent) {
        api.dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        int ans;
        if (agp.get(agent.getID()) == null) {
            List<node_data> lst = nextPokemonByDis(ga, src, agent);
            if (lst.isEmpty()) {
                int dest = ga.getGraph().getE(agent.getSrcNode()).iterator().next().getDest();
                ans = dest;
            } else {
                ans = lst.remove(0).getKey();
            }

        } else {
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

    private static synchronized List<node_data> nextPokemonByDisAndVal(dw_graph_algorithms g, int src, CL_Agent agent) {
        System.out.println("im here agent num " + agent.getID());
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }

        Iterator<CL_Pokemon> itr = poks.listIterator();
        CL_Pokemon closer = null;
        double max = 0;
        double val;
        List<node_data> lst;
        List<node_data> lst2 = null;
        while (itr.hasNext()) {
            CL_Pokemon current = itr.next();
            System.out.println("try" + current);
            edge_data e = current.get_edge();
            int type = current.getType();
            double d;
            if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                d = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
                lst = g.shortestPath(src, e.getSrc());
                lst.add(g.getGraph().getNode(e.getDest()));
            } else {
                d = g.shortestPathDist(src, e.getDest()) + e.getWeight();
                lst = g.shortestPath(src, e.getDest());
                lst.add(g.getGraph().getNode(e.getSrc()));
            }
            val = current.getValue();
            lst.remove(0);
            if (val / d > max) {
                max = val / d;
                closer = current;
                lst2 = lst;
            }
        }
        //agp.replace(agent.getID(), closer.getLocation());
        System.out.println("agent: " + agent.getID() + " pick " + closer);
        return lst2;
    }

    private static synchronized List<node_data> nextPokemonByDis(dw_graph_algorithms g, int src, CL_Agent agent) {
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        List<CL_Pokemon> updatedPoks = new LinkedList<>();
        List<CL_Pokemon> notYuorPoks = new LinkedList<>();
        Collection<Point3D> c = agp.values();
        boolean flag;
        for (CL_Pokemon pok : poks) {
            flag = false;
            for (Point3D p : c) {
                if (p != null) {
                    if (p.equals(pok.getLocation())) {
                        flag = true;
                        notYuorPoks.add(pok);
                    }
                }
            }
            if (!flag) {
                updatedPoks.add(pok);
            }
        }
        List<CL_Pokemon> updated2Poks = goodList(updatedPoks, g, agent);
        if (updated2Poks.isEmpty()) {
            List<node_data> emptyList = new LinkedList<>();
            return emptyList;
        }
        Iterator<CL_Pokemon> itr = updated2Poks.listIterator();
        CL_Pokemon closer = null;
        double min = Double.MAX_VALUE;
        List<node_data> lst;
        List<node_data> lst2 = null;
        while (itr.hasNext()) {
            CL_Pokemon current = itr.next();
            edge_data e = current.get_edge();
            int type = current.getType();
            double d;
            if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                d = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
                lst = g.shortestPath(src, e.getSrc());
                lst.add(g.getGraph().getNode(e.getDest()));
            } else {
                d = g.shortestPathDist(src, e.getDest()) + e.getWeight();
                lst = g.shortestPath(src, e.getDest());
                lst.add(g.getGraph().getNode(e.getSrc()));
            }
            lst.remove(0);
            if (d < min) {
                min = d;
                closer = current;
                lst2 = lst;
            }
        }
        agp.replace(agent.getID(), closer.getLocation());
        return lst2;
    }

    private static List<node_data> continueToPok(dw_graph_algorithms g, int src, CL_Agent agent, CL_Pokemon current) {
        double d = Double.MAX_VALUE;
        List<node_data> maybeBetter = new LinkedList<>();
        if (numOfPoks > numOfAgs) {
            maybeBetter = nextPokemonByDis(g, src, agent);
            if (!maybeBetter.isEmpty()) {
                d = 0;
                for (int i = 0; i < maybeBetter.size() - 1; i++) {
                    node_data sorc = maybeBetter.get(i);
                    node_data dest = maybeBetter.get(i + 1);
                    d += g.getGraph().getEdge(sorc.getKey(), dest.getKey()).getWeight();
                }
            }
        }
        if (numOfAgs == numOfPoks) {
            List<CL_Pokemon> p = new LinkedList<>();
            p.add(current);
            List<CL_Pokemon> goodList = goodList(p, g, agent);
            if (goodList.isEmpty()) {
                agp.replace(agent.getID(), null);
                return maybeBetter;
            }
        }
        Arena.updateEdge(current, g.getGraph());
        List<node_data> lst;
        edge_data e = current.get_edge();
        double d2;
        int type = current.getType();
        if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
            d2 = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
            lst = g.shortestPath(src, e.getSrc());
            lst.add(g.getGraph().getNode(e.getDest()));
        } else {
            d2 = g.shortestPathDist(src, e.getDest()) + e.getWeight();
            lst = g.shortestPath(src, e.getDest());
            lst.add(g.getGraph().getNode(e.getSrc()));
        }
        lst.remove(0);
        if (d < d2) {
            lst = maybeBetter;
        } else {
            agp.replace(agent.getID(), current.getLocation());
        }
        return lst;
    }


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

    public static List<CL_Pokemon> goodList(List<CL_Pokemon> good, dw_graph_algorithms g, CL_Agent current) {
        List<CL_Pokemon> lst = good;
        Iterator<CL_Pokemon> goodItr = lst.listIterator();
        Iterator<CL_Agent> agItr = _ar.getAgents().listIterator();
        List<CL_Pokemon> goodPoks = new LinkedList<>();
        boolean flag;
        while (goodItr.hasNext()) {
            flag = true;
            CL_Pokemon goodPok = goodItr.next();
            int type = goodPok.getType();
            edge_data e = goodPok.get_edge();
            double d2;
            if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                d2 = g.shortestPathDist(current.getSrcNode(), goodPok.get_edge().getSrc()) + e.getWeight();
            } else {
                d2 = g.shortestPathDist(current.getSrcNode(), goodPok.get_edge().getDest()) + e.getWeight();
            }
            while (agItr.hasNext()) {
                CL_Agent agent = agItr.next();
                if (agent != current) {
                    double d1;
                    if (agp.get(agent.getID()) != null) {
                        CL_Pokemon pok = _ar.getPokByPoint(agp.get(agent.getID()));
                        int type2 = pok.getType();
                        edge_data e2 = pok.get_edge();
                        if (e2.getDest() > e2.getSrc() && type2 == 1 || e2.getDest() < e2.getSrc() && type2 == -1) {
                            d1 = g.shortestPathDist(pok.get_edge().getDest(), goodPok.get_edge().getSrc()) + e.getWeight();
                        } else {
                            d1 = g.shortestPathDist(pok.get_edge().getDest(), goodPok.get_edge().getDest()) + e.getWeight();
                        }
                    } else {
                        if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                            d1 = g.shortestPathDist(agent.getSrcNode(), goodPok.get_edge().getSrc()) + e.getWeight();
                        } else {
                            d1 = g.shortestPathDist(agent.getSrcNode(), goodPok.get_edge().getDest()) + e.getWeight();
                        }
                    }
                    if (d1 < d2) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                goodPoks.add(goodPok);
            }
            agItr = _ar.getAgents().listIterator();
        }
        return goodPoks;
    }

    private CL_Pokemon choosePokemon1(List<CL_Pokemon> pokList, dw_graph_algorithms g) {
        Iterator<CL_Pokemon> poks = pokList.listIterator();
        Iterator<CL_Pokemon> poks2 = pokList.listIterator();
        CL_Pokemon chosen = null;
        double d = 0;
        double d1 = 1;
        while (poks.hasNext()) {
            CL_Pokemon pok = poks.next();
            edge_data e = pok.get_edge();
            int key1;
            if (e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) {
                key1 = e.getSrc();
            } else {
                key1 = e.getDest();
            }
            while (poks2.hasNext()) {
                CL_Pokemon pok2 = poks2.next();
                if (pok != pok2) {
                    edge_data e2 = pok2.get_edge();
                    int key2;
                    if (e2 == e) {
                        key2 = key1;
                    } else if (e2.getDest() > e2.getSrc() && pok2.getType() == 1 || e2.getDest() < e2.getSrc() && pok2.getType() == -1) {
                        key2 = e.getSrc();
                    } else {
                        key2 = e.getDest();
                    }
                    d1 += g.shortestPathDist(key1, key2);
                }
            }
            if (pok.getValue() / d1 > d) {//&& getRatio(e,g.getGraph(),pok.getLocation()) > 0.15) {
                chosen = pok;
                d = pok.getValue() / d1;
            }
            d1 = 1;
            poks2 = pokList.listIterator();
        }
        return chosen;
    }


    public double getRatio(edge_data e, directed_weighted_graph g, Point3D point) {
        if (e != null) {
            int src = e.getSrc();
            int dest = e.getDest();
            double ratio = g.getNode(src).getLocation().distance(point) / g.getNode(src).getLocation().distance(g.getNode(dest).getLocation());
            return ratio;
        }
        return -1;
    }
}

