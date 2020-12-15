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


public class Ex22 implements Runnable {
    private static MyFrame2 _win;
    private static Arena _ar;
    private long id;
    private int scenario;
    private static HashMap<Integer, Point3D> agp;

    public static void main(String[] args) {
        //loginMenu login = new loginMenu();
        //login.chose();
        //while(login.flag != true) {
        //    System.out.print("");
        //}
        Ex22 start = new Ex22(316485176, 4);
        Thread client = new Thread(start);
        client.start();
    }

    public Ex22(long id, int scenario) {
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
                Thread.sleep(dt);
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
            for (int i = 0; i < poks.size(); i++) {
                Arena.updateEdge(poks.get(i), gg);
            }
            PriorityQueue<CL_Pokemon> pq = mostValuePok(poks);
            agp = new HashMap<>();
            for (int i = 0; i < numOfAgents && !pq.isEmpty(); i++) {
                // int n = i % poks.size();
                // CL_Pokemon pok = poks.get(n);
                CL_Pokemon pok = pq.poll();
                edge_data e = pok.get_edge();
                int dest;
                if (e.getDest() > e.getSrc() && pok.getType() == 1 || e.getDest() < e.getSrc() && pok.getType() == -1) {
                    dest = e.getSrc();
                } else {
                    dest = e.getDest();
                }
                game.addAgent(dest);
                System.out.println(pok + " edge: " + e);
                agp.put(i, pok.getLocation());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gg;
    }


    private void moveAgents(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        System.out.println(lg);
        List<CL_Agent> log = Arena.getAgents(lg, gg);
        _ar.setAgents(log);
        String fs = game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        boolean flag;
        for (CL_Agent ag : log) {
            flag = false;
            Point3D p = agp.get(ag.getID());
            if(p != null) {
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
            ans = lst.remove(0).getKey();
        } else {
            CL_Pokemon current = _ar.getPokByPoint(agp.get(agent.getID()));
            List<node_data> lst = continueToPok(ga,src,agent,current);
            ans = lst.remove(0).getKey();
        }
        return ans;
    }

    /*
        private static synchronized List<node_data> nextPokemonByDisAndVal(dw_graph_algorithms g, int src, CL_Agent agent) {
            List<CL_Pokemon> poks = _ar.getPokemons();
            for (CL_Pokemon pok : poks) {
                Arena.updateEdge(pok, g.getGraph());
            }
            List<CL_Pokemon> updatedList = new LinkedList<>();
            for (CL_Pokemon pok : poks) {
                if (!pok.getIsEaten()) {
                    updatedList.add(pok);
                }
            }
            Iterator<CL_Pokemon> itr = updatedList.listIterator();
            CL_Pokemon closer = null;
            double min = -1;
            List<node_data> lst;
            List<node_data> lst2 = null;
            while (itr.hasNext()) {
                CL_Pokemon current = itr.next();
                if (!current.getIsEaten()) {
                    System.out.println("try" + current);
                    edge_data e = current.get_edge();
                    int type = current.getType();
                    double d, v;
                    if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                        d = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
                        v = current.getValue();
                        lst = g.shortestPath(src, e.getSrc());
                        lst.add(g.getGraph().getNode(e.getDest()));
                    } else {
                        d = g.shortestPathDist(src, e.getDest()) + e.getWeight();
                        v = current.getValue();
                        lst = g.shortestPath(src, e.getDest());
                        lst.add(g.getGraph().getNode(e.getSrc()));
                    }
                    lst.remove(0);
                    if (v / d > min) {
                        min = v / d;
                        closer = current;
                        lst2 = lst;
                    }
                }
            }
            closer.setIsEaten();
            agent.setChoosen(closer);
            System.out.println("agent: " + agent.getID() + " pick " + closer);
            return lst2;
        }
    */
    private static synchronized List<node_data> nextPokemonByDis(dw_graph_algorithms g, int src, CL_Agent agent) {
        System.out.println("im here agent num " + agent.getID());
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        List<CL_Pokemon> updatedPoks = new LinkedList<>();
        Collection<Point3D> c = agp.values();
        boolean flag;
        for(CL_Pokemon pok : poks){
            flag = false;
            for(Point3D p : c) {
                if (p != null) {
                    if (p.equals(pok.getLocation())) {
                        flag = true;
                    }
                }
            }
            if(!flag){
                updatedPoks.add(pok);
            }
        }

        Iterator<CL_Pokemon> itr = updatedPoks.listIterator();
        CL_Pokemon closer = null;
        double max = Double.MAX_VALUE;
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
            lst.remove(0);
            if (d < max) {
                max = d;
                closer = current;
                lst2 = lst;
            }
        }
        agp.replace(agent.getID(),closer.getLocation());
        System.out.println("agent: " + agent.getID() + " pick " + closer);
        return lst2;
    }

    private static synchronized List<node_data> continueToPok(dw_graph_algorithms g, int src, CL_Agent agent, CL_Pokemon current) {
        Arena.updateEdge(current, g.getGraph());
        List<node_data> lst;
        edge_data e = current.get_edge();
        int type = current.getType();
        double d;
        if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
            lst = g.shortestPath(src, e.getSrc());
            lst.add(g.getGraph().getNode(e.getDest()));
        } else {
            lst = g.shortestPath(src, e.getDest());
            lst.add(g.getGraph().getNode(e.getSrc()));
        }
        lst.remove(0);

        System.out.println("agent: " + agent.getID() + " pick " + current);
        return lst;
    }

    public PriorityQueue<CL_Pokemon> mostValuePok(ArrayList<CL_Pokemon> poks) {
        PriorityQueue<CL_Pokemon> pq = new PriorityQueue<>(poks.size(), (o1, o2) -> {
            if (o1.getValue() < o2.getType()) {
                return 1;
            } else if (o1.getValue() > o2.getType()) {
                return -1;
            } else {
                return 0;
            }
        });
        pq.addAll(poks);
        return pq;
    }
}

