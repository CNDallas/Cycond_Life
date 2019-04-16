package com.example.cycondlife;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/*
    This should be a singleton there should only be one player
 */
public class Player extends Character {
    private static Player player_instance;
    private String username;
    private String password;
    private int id;
    private ChatSender sender;
    private URI chatLink;

    private Player()
    {
        super();
    }



    private static int monstersKilled;
    private final String statlink="/api/stats/updateStat/";

    private Context context;
    private Callback_handler callback;
    private int experiance=0;
    private int level =1;

    private int hitChance =100;
    //TODO make this private
    public double sight =.002;
    private double critChance =1;
    private double critMult =2;
    private double dmgReduct =.1;
    private double BS =10;
    private int tinkPointsMax =50;
    private double tinkMult=1.0;
    private double dodgeChance=15;
    private int tinkeringPoints=-1;
    private ArrayList<Item> inv = new ArrayList<>();
    private ArrayList<Consumable> activeItems = new ArrayList<>();

    private int itemCount=0;


    public int getHitChance() {
        return hitChance;
    }
    public double getSight()
    {
        return sight;
    }
    public double getDodgeChance()
    {
        return dodgeChance;
    }
    public double getCritChance()
    {
        return critChance;
    }
    public double getCritMult()
    {
        return critMult;
    }
    public double getDmgReduct()
    {
        return  dmgReduct;
    }
    public double getBS()
    {
        return BS;
    }
    public int getTinkPointsMax()
    {
        return tinkPointsMax;
    }

    public double getTinkMult()
    {
        return tinkMult;
    }
    public double getTinkeringPoints()  {
        return tinkeringPoints;
    }
    public int getLevel() {
        return level;
    }
    public void incEXP(int val)
    {
        experiance+=val;
    }
    public int getCreativity(){return super.creativity;}
    public void addActiveItem(Item i)
    {
        activeItems.add((Consumable) i);
    }
    public ArrayList<Consumable> getActives()
    {
        return activeItems;
    }
    private Player(String user, int idt)
    {

        super();
        //int itemID,String name,String desc,int type,Dice effect,int duration,String use_msg
        update_substats();
        this.id=idt;
        this.username=user;
    }
    /*
    should be used only for test methods
     */
    public static synchronized void createTestInstance(String user, int idt)
    {
        player_instance = new Player(user,idt);
    }
    private Player(String user, int idt, Context c)
    {
        super();
        Consumable c1 =new Consumable(0,"lesser health potion","This potion sits in a red bottle labeled TEST",0,new Dice("2+2d4"),0,"You take a health Potion");
        Item.itemList.add(new Consumable(002,"Test Duration","This item should be active for a little while",2,new Dice(("4+0d4")),5,"you drink the test potion you feel more powerful"));
        Item.itemList.add(c1);
        update_substats();

        username=user;
        name=user;

        //Connect to chat websocket for persistent chat
        try {
            chatLink = new URI("ws://cs309-sd-6.misc.iastate.edu:8080/websocket/" + username);
        }
        catch (URISyntaxException e)    {
            e.printStackTrace();
        }

        sender = new ChatSender();
        sender.connectWebSocket(chatLink);

        this.id=idt;
        this.context = c;
        callback = new Callback_handler() {
            @Override
            public void get_response(JSONArray a) {
                for (int i = 0; i < a.length(); i++)
                {
                    try {


                        if (a.getJSONObject(i).getInt("accountId")==id)
                        {
                            get_stats(a.getJSONObject(i).getInt("id"),this,context);
                        }
                    }
                    catch (Exception e) {
                        Log.i("Cycond error", "error getting user info");
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void get_object_response(JSONObject o) {
                try {
                   presentation = o.getInt("presentation");
                   monstersKilled = o.getInt("monstersKilled");
                    critical_thinking = o.getInt("critical thinking");
                    creativity = o.getInt("creativity");
                    BS=presentation+critical_thinking;
                    resolve= presentation;
                }
                catch(Exception e)
                {
                    Log.i("Cycond Life","Stat pull error");
                }
            }
        };
        //TODO this could be made more efficant
        get_stats(id,callback,c);
      //  RequestQueue q = new Volley.newRequestQueue(c);
       // JsonObjectRequest j = new JsonObjectRequest()
    }

    public static Player get_instance()
    {
        return player_instance;
    }
    public static int getMonstersKilled(){return monstersKilled;}
    public void force_update()
    {
        get_stats(id,callback,context);
    }

    public ArrayList<Item> getInv() {
        return inv;
    }
    public void addItem(Item i)
    {
        //TODO propagate to the server when possuible
        if(inv.size()<20)
        {
            inv.add(i);
        }
        else
        {
            Log.i("Cycond Info", "You drop some items");
        }
    }
    public Item removeItem(int index)
    {
        return inv.remove(index);
    }
    public void parseActives()
    {
        for(int i=0;i<activeItems.size();i++)
        {
            Consumable c=activeItems.get(i);
            switch (c.type)
            {
                case Consumable.creativity:
                {
                    Log.i("Cycond Info", "Creativity: "+creativity);
                    creativity+=c.getEffect().roll();
                    Log.i("Cycond Info", "Creativity: "+creativity);
                    break;
                }
                case Consumable.presentation: {
                    Log.i("Cycond Info", "presentation: "+presentation);
                    presentation += c.getEffect().roll();
                    Log.i("Cycond Info", "presentation: "+presentation);
                    break;
                }
                case Consumable.criticalThinking: {
                    Log.i("Cycond Info", "criticalThinking: "+critical_thinking);
                    critical_thinking += c.getEffect().roll();
                    Log.i("Cycond Info", "criticalThinking: "+critical_thinking);

                    break;
                }
                default:
                    Log.i("Cycond Error","unhandled item type please contact the developer");
                break;

            }

        }
    }
    public void update_substats()
    {
        parseActives();
        hitChance =50+creativity+critical_thinking;
        if(hitChance >99) hitChance =99;
        sight =.001+(critical_thinking+0.0)/10000;
        critChance=1+((critical_thinking+creativity)/1000.0)*9;
        critMult= 2+ (presentation+critical_thinking)/500.0;
        dmgReduct = .01 +(presentation+critChance)/100.0;
        BS=(presentation+critical_thinking)/100.0;
        tinkPointsMax =(int) Math.round(1.5*critical_thinking);
        tinkMult=.9+(creativity+critical_thinking)/1500.0;
        dodgeChance= 15+(creativity/2000.0);
        if(tinkeringPoints==-1) tinkeringPoints=tinkPointsMax;
    }

    public static synchronized void create_the_instance(String user,int id,Context c)
    {
        if(player_instance!=null)
        {
            return;
        }
        player_instance = new Player(user,id,c);
    }

    public static synchronized void destroy_the_instance()
    {
        player_instance=null;
    }


    private void get_stats(int statsId,final Callback_handler c,Context t)
        {
            RequestQueue r =  Volley.newRequestQueue(t);
            JsonObjectRequest o = new JsonObjectRequest(Request.Method.GET,"http://cs309-sd-6.misc.iastate.edu:8080/api/stats/"+statsId,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Do something with response
                    //mTextView.setText(response.toString());

                    // Process the JSON
                    Log.i("Cycond test", "user stats request succsessful");
                    c.get_object_response(response);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when error occurred
                            Log.i("Cycond Life", "user stats error");
                            Log.i("Cycond Life", error.toString());
                        }
                    });
        r.add(o);
    }

    public ChatSender getSender() {
        return sender;
    }

    public void take_dmg(int dmg,Context c)
    {
       resolve=this.resolve-dmg;
        Json_handler j = new Json_handler(c);
        j.update_stat(Player.get_instance().id,"resolve",resolve);
    }
    /*
    TODO adjust this to update locally as well
     */
    protected void update_stat(int val, String stat, Context c)
    {
        Json_handler j = new Json_handler(c);
        j.update_stat(id,stat,val);
    }
    public void changeResolve(int i)
    {
        resolve +=i;
        if(resolve>=100)
        {
            resolve=100;
        }
        Json_handler j = new Json_handler(context);
        j.update_stat(Player.get_instance().id,"resolve",resolve);
    }
    public void adjustTinkeringPoints(int i)
    {
        tinkeringPoints +=i;
        if((int) Math.round(1.5*critical_thinking)< tinkeringPoints) tinkeringPoints =(int) Math.round(1.5*critical_thinking);
    }
    public void endItem(Consumable c)
    {
        switch (c.type)
        {
            case Consumable.creativity:
            {
                creativity-=c.getEffect().roll();
                break;
            }
            case Consumable.presentation: {
                presentation -= c.getEffect().roll();
                break;
            }
            case Consumable.criticalThinking: {
                critical_thinking -= c.getEffect().roll();

                break;
            }
            default:
                Log.i("Cycond Error","unhandled item type please contact the developer");
                break;

        }
    }
}
