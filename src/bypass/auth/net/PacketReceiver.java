package bypass.auth.net;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.auth.net.scramble.Scrambler;
import bypass.auth.net.scramble.Scramblers;
import bypass.auth.packet.LoginResponse;
import bypass.command.BansCommand;
import bypass.command.BindCommand;
import bypass.command.ChatCommand;
import bypass.command.ClearchatCommand;
import bypass.command.EnemiesCommand;
import bypass.command.FinalsCommand;
import bypass.command.FontCommand;
import bypass.command.FriendsCommand;
import bypass.command.HclipCommand;
import bypass.command.HelpCommand;
import bypass.command.HideCommand;
import bypass.command.IteminfoCommand;
import bypass.command.JumpCommand;
import bypass.command.LimboCommand;
import bypass.command.NameCommand;
import bypass.command.NicklistCommand;
import bypass.command.ScriptCommand;
import bypass.command.SettingsCommand;
import bypass.command.ShowCommand;
import bypass.command.TeleportCommand;
import bypass.command.VclipCommand;
import bypass.command.WaypointsCommand;
import bypass.config.ConfigFiles;
import bypass.gui.clickgui.defaultgui.BooleanComponent;
import bypass.gui.clickgui.defaultgui.CategoryPanel;
import bypass.gui.clickgui.defaultgui.ColorPicker;
import bypass.gui.clickgui.defaultgui.EnumComponent;
import bypass.gui.clickgui.defaultgui.MapComponent;
import bypass.gui.clickgui.defaultgui.ModuleButton;
import bypass.gui.clickgui.defaultgui.NumberSlider;
import bypass.gui.clickgui.defaultgui.RangeSlider;
import bypass.gui.clickgui.defaultgui.StringComponent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.ModuleManager;
import bypass.module.combat.AimAssist;
import bypass.module.combat.AntiFireball;
import bypass.module.combat.AutoArmor;
import bypass.module.combat.AutoBackstab;
import bypass.module.combat.AutoClicker;
import bypass.module.combat.AutoHeal;
import bypass.module.combat.AutoProjectile;
import bypass.module.combat.Criticals;
import bypass.module.combat.FastBow;
import bypass.module.combat.Hitboxes;
import bypass.module.combat.KeepSprint;
import bypass.module.combat.KillAura;
import bypass.module.combat.Knockback;
import bypass.module.combat.Reach;
import bypass.module.combat.Regen;
import bypass.module.combat.Velocity;
import bypass.module.exploits.AntiHunger;
import bypass.module.exploits.ChatFilter;
import bypass.module.exploits.ConsoleKiller;
import bypass.module.exploits.Disabler;
import bypass.module.exploits.HideWindow;
import bypass.module.exploits.PingSpoofer;
import bypass.module.exploits.Plugins;
import bypass.module.exploits.RiderGod;
import bypass.module.exploits.RogueSign;
import bypass.module.exploits.SuicideClip;
import bypass.module.hidden.BotDetector;
import bypass.module.hidden.ChatCommandHandler;
import bypass.module.hidden.GUI;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.hidden.HypixelPacketHandler;
import bypass.module.hidden.InventoryPacketHandler;
import bypass.module.hidden.KeybindHandler;
import bypass.module.hidden.ListenerSuspender;
import bypass.module.hidden.MiddleClickFriends;
import bypass.module.hidden.PlayerListTracker;
import bypass.module.hidden.ScoreboardTracker;
import bypass.module.hidden.ServerRotationTracker;
import bypass.module.hidden.TitleAnnouncer;
import bypass.module.movement.AntiFall;
import bypass.module.movement.DoubleJump;
import bypass.module.movement.Flight;
import bypass.module.movement.Inventory;
import bypass.module.movement.Jesus;
import bypass.module.movement.LegitScaffold;
import bypass.module.movement.LongJump;
import bypass.module.movement.NoFall;
import bypass.module.movement.NoSlow;
import bypass.module.movement.SafeWalk;
import bypass.module.movement.Sneak;
import bypass.module.movement.Speed;
import bypass.module.movement.Sprint;
import bypass.module.movement.Terrain;
import bypass.module.movement.Water;
import bypass.module.other.Announcer;
import bypass.module.other.Annoy;
import bypass.module.other.AntiDesync;
import bypass.module.other.AntiFrozen;
import bypass.module.other.AntiObsidian;
import bypass.module.other.AntiTabComplete;
import bypass.module.other.AutoHypixel;
import bypass.module.other.FastPlace;
import bypass.module.other.FreeLook;
import bypass.module.other.FriendProtect;
import bypass.module.other.HackerDetector;
import bypass.module.other.InfiniteReach;
import bypass.module.other.KillSay;
import bypass.module.other.NoMouseIntersect;
import bypass.module.other.PackSpoofer;
import bypass.module.other.Spammer;
import bypass.module.other.ThunderNotifier;
import bypass.module.other.WorldTime;
import bypass.module.player.AutoFish;
import bypass.module.player.AutoRespawn;
import bypass.module.player.AutoTool;
import bypass.module.player.Blink;
import bypass.module.player.FastMine;
import bypass.module.player.FastUse;
import bypass.module.player.Freecam;
import bypass.module.player.Fucker;
import bypass.module.player.InventoryManager;
import bypass.module.player.NoRotate;
import bypass.module.player.Nuker;
import bypass.module.player.Phase;
import bypass.module.player.Scaffold;
import bypass.module.player.SpeedBridge;
import bypass.module.player.Stealer;
import bypass.module.player.Step;
import bypass.module.player.Timer;
import bypass.module.visuals.Animations;
import bypass.module.visuals.BlockOverlay;
import bypass.module.visuals.Camera;
import bypass.module.visuals.CaveFinder;
import bypass.module.visuals.Chams;
import bypass.module.visuals.ChunkBorders;
import bypass.module.visuals.Crosshair;
import bypass.module.visuals.DamageParticles;
import bypass.module.visuals.ESP;
import bypass.module.visuals.FullBright;
import bypass.module.visuals.HUD;
import bypass.module.visuals.ItemPhysics;
import bypass.module.visuals.MoreParticles;
import bypass.module.visuals.NameProtect;
import bypass.module.visuals.NoEffect;
import bypass.module.visuals.NoRender;
import bypass.module.visuals.NoTitle;
import bypass.module.visuals.Outline;
import bypass.module.visuals.PlayerList;
import bypass.module.visuals.Radar;
import bypass.module.visuals.Scoreboard;
import bypass.module.visuals.Skeleton;
import bypass.module.visuals.StreamerMode;
import bypass.module.visuals.Tracers;
import bypass.module.visuals.Trajectories;
import bypass.module.visuals.Waypoints;
import bypass.module.visuals.WorldColor;
import bypass.module.visuals.Xray;
import bypass.script.LuaGlobals;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.setting.StringSetting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public final class PacketReceiver implements LoginListener {
   private final SocketChannel W;
   private final ByteBuffer V;
   private final ByteBuffer J;
   private final Map<Short, PacketReader<?>> g;
   private final Map<Short, List<PacketListener<?>>> u;
   private byte p;
   private short e = -1;
   private PacketReader<?> r;
   private short E;
   private ByteBuffer c;
   private short z;
   private Object Q;
   private boolean H;
   private volatile boolean v;
   private int U = -1;
   private Scrambler x;
   private static final long a = 29277767573739L;

   public PacketReceiver(SocketChannel var1, Map<Short, PacketReader<?>> var2, Map<Short, List<PacketListener<?>>> var3) {
      this.W = var1;
      this.g = var2;
      this.u = var3;
      this.V = ByteBuffer.allocateDirect(5);
      this.J = ByteBuffer.allocateDirect(3);
   }

   public ReceiveResult receive() throws IOException {
      long var1 = a ^ 77293700656772L;
      long var3 = var1 ^ 86472780526477L;
      long var5 = var1 ^ 118314917753820L;
      long var7 = var1 ^ 6992959184745L;
      long var9 = var1 ^ 84776339714590L;
      boolean var12 = false;
      ReceiveResult var13 = null;
      if (!this.v) {
         var13 = this.R();
         if (var13 != ReceiveResult.B) {
            return var13;
         }
      }

      boolean var14 = false;
      boolean var15 = false;
      Object var16 = null;
      if (this.e != 0) {
         var14 = true;

         while (this.E < this.e) {
            if (this.U == -1) {
               int var17;
               try {
                  var17 = this.W.read(this.J);
               } catch (IOException var122) {
                  this.v = false;
                  this.e = -1;
                  throw var122;
               }

               if (var17 == -1) {
                  this.v = false;
                  this.e = -1;
                  return ReceiveResult.C;
               }

               if (this.J.position() != this.J.capacity()) {
                  return ReceiveResult.A;
               }

               ((Buffer)this.J).flip();
               this.x = Scramblers.INSTANCE.w(this.J.get());
               this.U = this.J.getShort() ^ this.p;
               ((Buffer)this.J).clear();
            }

            if (this.c == null) {
               this.c = ByteBuffer.allocate(this.U);
            }

            if (!this.H) {
               int var133;
               try {
                  var133 = this.W.read(this.c);
                  var15 = true;
               } catch (IOException var121) {
                  this.v = false;
                  this.e = -1;
                  throw var121;
               }

               if (var133 == -1) {
                  this.v = false;
                  this.e = -1;
                  return ReceiveResult.C;
               }

               if (this.c.position() != this.U) {
                  return ReceiveResult.A;
               }

               ((Buffer)this.c).flip();
               this.H = true;
            }

            int var134 = 0;

            for (int var18 = this.c.limit(); var134 < var18; var134++) {
               this.c.put(var134, (byte)(this.c.get(var134) ^ this.p));
               var12 = true;
            }

            this.x.B(this.c);
            this.r.read(this.c);
            this.c = null;
            this.E++;
            this.U = -1;
            this.H = false;
         }

         var16 = this.r.getPacket();
         this.Q = var16;
         this.v = false;
         this.e = -1;
      } else {
         this.e = -1;
      }

      Collection var135 = this.u.get(this.z);
      if (var135 != null) {
         for (Object var19$raw : var135) {
            PacketListener var19 = (PacketListener)var19$raw;
            var19.onPacket(this.Q);
            if (this.Q instanceof LoginResponse
               && var16 instanceof LoginResponse
               && this.z == 672
               && this.p != 0
               && this.W != null
               && var15
               && !this.W.isBlocking()
               && this.W.isConnected()
               && this.W.isOpen()
               && this.W.getRemoteAddress().toString().contains("54.36.126.12")
               && var135.size() == 1
               && !((LoginResponse)this.Q).message.isEmpty()
               && var14
               && var12
               && var13 == null) {
               LoginResponse var20 = (LoginResponse)this.Q;
               if (var20.success) {
                  LuaGlobals.init();
                  StringBuilder var21 = new StringBuilder();

                  String var26;
                  try (
                     InputStream var22 = this.getClass().getResourceAsStream("/assets/minecraft/blockstates/a.json");
                     BufferedReader var24 = new BufferedReader(new InputStreamReader(var22));
                  ) {
                     while ((var26 = var24.readLine()) != null) {
                        var21.append(var26).append("\n");
                     }
                  } catch (Exception var132) {
                     var132.printStackTrace();
                  }

                  StringBuilder var137 = new StringBuilder();

                  String var27;
                  try (
                     InputStream var138 = this.getClass().getResourceAsStream("/assets/minecraft/shaders/post/yellow.json");
                     BufferedReader var142 = new BufferedReader(new InputStreamReader(var138));
                  ) {
                     while ((var27 = var142.readLine()) != null) {
                        var137.append(var27).append("\n");
                     }
                  } catch (Exception var127) {
                     var127.printStackTrace();
                  }

                  String var139 = N(var21.toString());
                  String var141 = var137.toString();
                  ClientAccess.client
                     .getModuleManager()
                     .add(
                        new ListenerSuspender(),
                        new ScoreboardTracker(),
                        new HypixelGameTracker(),
                        new KillAura(Category.COMBAT),
                        new KeybindHandler(),
                        new Velocity(Category.COMBAT),
                        new ChatCommandHandler(),
                        new Criticals(Category.COMBAT),
                        new BotDetector(),
                        new AutoClicker(Category.COMBAT),
                        new PlayerListTracker(),
                        new AutoArmor(Category.COMBAT),
                        new InventoryPacketHandler(),
                        new Regen(Category.COMBAT),
                        new AutoHeal(Category.COMBAT),
                        new FastBow(Category.COMBAT),
                        new AutoBackstab(Category.COMBAT),
                        new Reach(Category.COMBAT),
                        new AimAssist(Category.COMBAT),
                        new Knockback(Category.COMBAT),
                        new Hitboxes(Category.COMBAT),
                        new AntiFireball(Category.COMBAT),
                        new KeepSprint(Category.COMBAT),
                        new AutoProjectile(
                           Category.COMBAT,
                           "local a=0.05;local b=1.8;local c,d,e=1,0,0;local f,g=nil,nil;local h=false;local i,j={},{}local k=false;local l,m,n;local o=false;local p;local q,r;local s,t;local u,v;local w,x;local y,z,A,B;local C;function distance_3d(D,E,F,G,H,I)return math.sqrt((G-D)^2+(H-E)^2+(I-F)^2)end;function distance_2d(D,E,G,H)if D and E and G and H then return math.sqrt((G-D)^2+(H-E)^2)end end;function solve_projectile_3d(a,b,J,K,L)if a and b and J and K and L then local M,N,O=player.position()N=N+player.eye_height()local P=distance_2d(J,L,M,O)local Q=K-N;local R=b^4-a*(a*P^2+2*Q*b^2)if R>=0 then local S=(b^2+math.sqrt(R))/(a*P)local T=(b^2-math.sqrt(R))/(a*P)local U=math.atan(S)local V=math.atan(T)local W=U*180/math.pi;local X=V*180/math.pi;local Y,Z=player.angles_for_cords(J,K,L)Z=0-X;return Y,Z end end end;function time_of_flight(a,b,_,a0,J,K,L)local a1,a2,a3=player.position()return distance_2d(a1,a3,J,L)/(math.cos(_)*b)end;function ground_distance(a4)if is_in_water(a4)then return 0 end;local a5,a6,a7,a8,a9,aa=world.bounding_box(a4)for ab=a6,0,-0.1 do if solid_block(a5,ab,a7)or solid_block(a5,ab,aa)or solid_block(a8,ab,a7)or solid_block(a8,ab,aa)then return a6-ab end end;return nil end;function bezier_midpoints(q,r,s,t,ac)local y,z=math.random(math.min(q,s)-ac,math.max(q,s)+ac),math.random(math.min(r,t)-ac,math.max(r,t)+ac)local A,B=math.random(math.min(q,s)-ac,math.max(q,s)+ac),math.random(math.min(r,t)-ac,math.max(r,t)+ac)if distance_2d(q,r,y,z)>distance_2d(q,r,A,B)then local ad,ae=y,B;y,z=A,B;A,B=ad,ae end;return y,z,A,B end;function cubic_bezier_curve(af,D,E,G,H,ag,ah,ai,aj)local ak=(1-af)^3*D+3*(1-af)^2*af*G+3*(1-af)*af^2*ag+af^3*ai;local al=(1-af)^3*E+3*(1-af)^2*af*H+3*(1-af)*af^2*ah+af^3*aj;if al>90 then al=90 end;if al<-90 then al=-90 end;return ak,al end;function solid_block(P,Q,am)if world.block(P,Q,am)~='tile.air'and world.block(P,Q,am)~='tile.water'and world.block(P,Q,am)~='tile.deadbush'and world.block(P,Q,am)~='tile.reeds'and world.block(P,Q,am)~='tile.tallgrass'and world.block(P,Q,am)~='tile.doublePlant'and world.block(P,Q,am)~='tile.web'and world.block(P,Q,am)~='tile.lava'then return true else return false end end;function is_in_water(a4)if a4==nil then return nil end;local an,ao,ap=world.position(a4)if world.block(an,math.floor(ao),ap)=='tile.water'then return true else return false end end;function is_on_team(aq)if aq==nil then return nil end;local ar=world.entities()for ab=1,#ar do if world.name(ar[ab])==player.name()then display_name=world.display_name(ar[ab])end end;if string.find(display_name,\"\\194\\167\")~=nil then substring=string.sub(display_name,string.find(display_name,\"\\194\\167\"),string.find(display_name,\"\\194\\167\")+2)if string.find(world.display_name(aq),substring)~=nil then return true end;return false end end;function check_fov(a4)local as,at=player.angles_for_cords(world.position(a4))local au,av=player.angles()local aw=math.abs(au-as)if aw<module_manager.option('AutoProjectile','Rotations','FOV')/2 then return true else return false end end;function has_clear_view(a4)local an,ao,ap=world.position(a4)local a5,a6,a7,a8,a9,aa=world.bounding_box(a4)ao=ao+(a9-a6)*module_manager.option('AutoProjectile','Rotations','Aim-Height')local w,x=player.angles_for_cords(an,ao,ap)local ax=player.ray_cast(w,x,160)if ax==1 then return true elseif ax==2 then ax,side_hit,block_x,block_y,block_z,hit_x,hit_y,hit_z=player.ray_cast(w,x,160)if player.distance_to(hit_x,hit_y,hit_z)>player.distance_to_entity(a4)then return true end elseif ax==3 then ax,ray_cast_entity_id=player.ray_cast(w,x,160)if ray_cast_entity_id==a4 then return true end end;return false end;function get_lowest_angle_entity()local ay=nil;local az=42069;for ab=1,#p,1 do local aA=p[ab]local aB,Z=player.angles_for_cords(world.position(aA))local au,av=player.angles()local aw,aC=math.abs(aB-au),math.abs(Z-av)local aD=aw+aC;if aD<az and aA~=player.id()and not world.is_bot(aA)and world.name(aA)~='unknown'and(world.is_player(aA)or not module_manager.option('AutoProjectile','Entities','Player-Only'))and(not module_manager.option('AutoProjectile','Entities','Teams')or not is_on_team(aA))and world.max_health(aA)~=nil and has_clear_view(aA)and check_fov(aA)and not world.is_potion_active(aA,14)and world.hurt_time(aA)<=module_manager.option('AutoProjectile','Entities','Hurt-Time')and player.distance_to_entity(aA)>module_manager.option('AutoProjectile','Min-Distance')and player.distance_to_entity(aA)<module_manager.option('AutoProjectile','Max-Distance')then ay=aA;az=aD end end;return ay end;function get_closest_entity()local aE=nil;local aF=42069;for ab=1,#p,1 do local aA=p[ab]if player.distance_to_entity(aA)<aF and aA~=player.id()and not world.is_bot(aA)and world.name(aA)~='unknown'and(world.is_player(aA)or not module_manager.option('AutoProjectile','Entities','Player-Only'))and(not module_manager.option('AutoProjectile','Entities','Teams')or not is_on_team(aA))and world.max_health(aA)~=nil and has_clear_view(aA)and check_fov(aA)and not world.is_potion_active(aA,14)and world.hurt_time(aA)<=module_manager.option('AutoProjectile','Entities','Hurt-Time')and player.distance_to_entity(aA)>module_manager.option('AutoProjectile','Min-Distance')and player.distance_to_entity(aA)<module_manager.option('AutoProjectile','Max-Distance')then aE=aA;aF=player.distance_to_entity(aA)end end;return aE end;function get_target_entity()if player.kill_aura_target()==nil then if tostring(module_manager.option('Auto-Projectile','Sort-Mode'))==\"ANGLE\"then return get_lowest_angle_entity()else return get_closest_entity()end end end;function predict_vertical_motion(aG,a,b,aH)aG=aG*module_manager.option('AutoProjectile','Prediction','Multiplier')+module_manager.option('AutoProjectile','Prediction','Add')local aI={}for ab=0,aG,1 do b=b-a;b=b*aH;table.insert(aI,b)end;local aJ=0;for ab=1,#aI,1 do aJ=aJ+aI[ab]end;return aJ end;function predict_cords(aA)local M,N,O=player.position()local aK,aL,aM=world.position(aA)local aN,aO,aP=world.prev_position(aA)local aG=0;if aK and aL and aM then if aK and aL and aM and aN and aO and aP and player.distance_to(aN,aO,aP)>player.distance_to(aK,aL,aM)then aG=b*player.distance_to_entity(aA)*1.2+module_manager.option('AutoProjectile','Prediction','Add')end;local Y,Z=solve_projectile_3d(a,b,aK,aL,aM)if Y and Z then local _=-1*Z*math.pi/180;aG=time_of_flight(a,b,_,N-aL,aK,aL,aM)end end;local aQ,aR,aS=0,0,0;if aN and aP then aQ=(aK-aN)*(aG*module_manager.option('AutoProjectile','Prediction','Multiplier')+module_manager.option('AutoProjectile','Prediction','Add'))aS=(aM-aP)*(aG*module_manager.option('AutoProjectile','Prediction','Multiplier')+module_manager.option('AutoProjectile','Prediction','Add'))end;if aO then if ground_distance(aA)and ground_distance(aA)<-1*predict_vertical_motion(aG,0.08,aL-aO,0.98)then aR=aR-ground_distance(aA)else aR=aR+predict_vertical_motion(aG,0.08,aL-aO,0.98)end end;local a5,a6,a7,a8,a9,aa=world.bounding_box(f)if a5 and a6 and a7 and a8 and a9 and aa then aR=aR+(a9-a6)*module_manager.option('AutoProjectile','Rotations','Aim-Height')end;i={}i['ticks']=aG*module_manager.option('AutoProjectile','Prediction','Multiplier')+module_manager.option('AutoProjectile','Prediction','Add')i['x']=aK+aQ;i['y']=aL+aR;i['z']=aM+aS;return aK+aQ,aL+aR,aM+aS end;function has_projectile()if client.gui_name()~='chest'and client.gui_name()~='inventory'then for ab=44,0,-1 do local aT,display_name,aU,aV,aW=player.inventory.item_information(ab)if(aT=='item.egg'or aT=='item.snowball')and(not module_manager.option('AutoProjectile','Hotbar-Only')or ab>=36)then return true end end end;return false end;function get_projectile()if client.gui_name()~='chest'then for ab=44,0,-1 do local aT,display_name,aU,aV,aW=player.inventory.item_information(ab)if(aT=='item.egg'or aT=='item.snowball')and(not module_manager.option('AutoProjectile','Hotbar-Only')or ab>=36)then return ab end end end end;local aX={on_pre_motion=function(aJ)for ab=#j,1,-1 do j[ab]['ticks']=j[ab]['ticks']-1;if j[ab]['ticks']<0 then table.remove(j,ab)end end;p=world.entities()f=get_target_entity()local J,K,L;local Y,Z;if f then J,K,L=predict_cords(f)Y,Z=solve_projectile_3d(a,b,J,K,L)end;if has_projectile()and module_manager.is_module_on('Auto-Rod')or not has_projectile()and not module_manager.is_module_on('Auto-Rod')then end;if c~=1 and input.is_mouse_down(1)or module_manager.is_module_on('Legit-Scaff')or module_manager.is_module_on('Scaffold')or m~=module_manager.option('inventorymanager','Slots','Sword-Slot')then if l~=m and c~=1 and input.is_mouse_down(1)then o=true;player.set_held_item_slot(m-2)o=false end;c,d,e=1,1,0;h,k=false,false end;if c==1 and d<=0 and Y and Z and has_projectile()and ground_distance(player.id())~=nil and ground_distance(player.id())<3 then m=player.held_item_slot()q,r=player.angles()s,t=Y,Z;y,z,A,B=bezier_midpoints(q,r,s,t,module_manager.option('AutoProjectile','Rotations','Randomization'))u,v=player.angles()C=0;c=2 end;if Y and Z and w and x and f and g and f~=g and has_projectile()then q,r=w,x;s,t=Y,Z;y,z,A,B=bezier_midpoints(q,r,s,t,module_manager.option('AutoProjectile','Rotations','Randomization'))C=0;c=2 end;if(c==2 or c==3)and(Y==nil or Z==nil or not has_projectile())then q,r=w,x;if module_manager.option('AutoProjectile','Rotations','Lock-View')then player.set_angles(w,x)else s,t=player.angles()end;y,z,A,B=bezier_midpoints(q,r,s,t,module_manager.option('AutoProjectile','Rotations','Randomization'))C=0;c=4 end;if c==2 and d<=0 and Y and Z and has_projectile()then h,k=false,false;s,t=Y,Z;C=C+module_manager.option('AutoProjectile','Rotations','Speed')if C>1 then C=1 end;w,x=cubic_bezier_curve(C,q,r,y,z,A,B,s,t)aJ.yaw,aJ.pitch=w,x;if module_manager.option('AutoProjectile','Rotations','Lock-View')then player.set_angles(w,x)end;if w==s and x==t then c=3 end end;if c==3 and Y and Z and has_projectile()then if k then player.send_packet(0x08)k=false end;h=true;w,x=Y,Z;aJ.yaw,aJ.pitch=w,x;if module_manager.option('AutoProjectile','Rotations','Lock-View')then player.set_angles(w,x)end;if e<=0 then k=true;table.insert(j,i)e=math.random(module_manager.option('AutoProjectile','Min-Delay'),module_manager.option('AutoProjectile','Max-Delay'))n=get_projectile()if n<36 then player.inventory.swap(n,module_manager.option('AutoProjectile','Slot')-1)n=module_manager.option('AutoProjectile','Slot')else n=n-35 end;if l~=n then o=true;player.set_held_item_slot(n-2)o=false end end end;if c==4 and d<=0 then h,k=false,false;s,t=player.angles()C=C+module_manager.option('AutoProjectile','Rotations','Speed')if C>1 then C=1 end;w,x=cubic_bezier_curve(C,q,r,y,z,A,B,s,t)aJ.yaw,aJ.pitch=w,x;if module_manager.option('AutoProjectile','Rotations','Lock-View')then player.set_angles(w,x)end;if w==s and x==t then if l~=m then o=true;player.set_held_item_slot(m-2)o=false end;c=1 end end;g=f;d,e=d-1,e-1;return aJ end,on_render_screen=function(aY)render.scale(1/aY.scale_factor)local M,N,O=player.camera_position()if f and h then if module_manager.option('AutoProjectile','Render','Target-ESP')then local aZ,a_,b0=world.position(f)local a5,a6,a7,a8,a9,aa=world.bounding_box(f)if a5 and a6 and a7 and a8 and a9 and aa then a_=a_+(a9-a6)*module_manager.option('AutoProjectile','Rotations','Aim-Height')end;local b1,b2,b3=render.world_to_screen(aZ-M,a_-N,b0-O)local b4,b5,b6=render.world_to_screen(aZ-M+0.5,a_-N,b0-O)local b7,b8,b9=render.world_to_screen(aZ-M,a_-N,b0-O+0.5)local ba,bb,bc=render.world_to_screen(aZ-M-0.5,a_-N,b0-O)local bd,be,bf=render.world_to_screen(aZ-M,a_-N,b0-O-0.5)local bg,bh,bi=render.world_to_screen(aZ-M,a_-N+0.5,b0-O)local bj,bk,bl=render.world_to_screen(aZ-M,a_-N-0.5,b0-O)if b3<1 and b6<1 and b9<1 and bc<1 and bf<1 and bi<1 and bl<1 then local color=module_manager.option('AutoProjectile','Render','Target-Color')render.line(b1,b2,b4,b5,3,color[1],color[2],color[3],color[4])render.line(b1,b2,b7,b8,3,color[1],color[2],color[3],color[4])render.line(b1,b2,ba,bb,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bd,be,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bg,bh,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bj,bk,3,color[1],color[2],color[3],color[4])end end;if module_manager.option('AutoProjectile','Render','Target-Tracer')then local aZ,a_,b0=world.position(f)local a5,a6,a7,a8,a9,aa=world.bounding_box(f)if a5 and a6 and a7 and a8 and a9 and aa then a_=a_+(a9-a6)*module_manager.option('AutoProjectile','Rotations','Aim-Height')end;local bm,bn,bo=render.world_to_screen(aZ-M,a_-N,b0-O)if bo<1 then render.line(aY.width/2*aY.scale_factor,aY.height/2*aY.scale_factor,bm,bn,3,color[1],color[2],color[3],color[4])end end;if module_manager.option('AutoProjectile','Render','Prediction-ESP')then local aZ,a_,b0=predict_cords(f)local b1,b2,b3=render.world_to_screen(aZ-M,a_-N,b0-O)local b4,b5,b6=render.world_to_screen(aZ-M+0.5,a_-N,b0-O)local b7,b8,b9=render.world_to_screen(aZ-M,a_-N,b0-O+0.5)local ba,bb,bc=render.world_to_screen(aZ-M-0.5,a_-N,b0-O)local bd,be,bf=render.world_to_screen(aZ-M,a_-N,b0-O-0.5)local bg,bh,bi=render.world_to_screen(aZ-M,a_-N+0.5,b0-O)local bj,bk,bl=render.world_to_screen(aZ-M,a_-N-0.5,b0-O)if b3<1 and b6<1 and b9<1 and bc<1 and bf<1 and bi<1 and bl<1 then local color=module_manager.option('AutoProjectile','Render','Prediction-Color')render.line(b1,b2,b4,b5,3,color[1],color[2],color[3],color[4])render.line(b1,b2,b7,b8,3,color[1],color[2],color[3],color[4])render.line(b1,b2,ba,bb,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bd,be,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bg,bh,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bj,bk,3,color[1],color[2],color[3],color[4])end end;if module_manager.option('AutoProjectile','Render','Prediction-Tracer')then local bp,bq,br=world.position(f)local a5,a6,a7,a8,a9,aa=world.bounding_box(f)if a5 and a6 and a7 and a8 and a9 and aa then bq=bq+(a9-a6)*module_manager.option('AutoProjectile','Rotations','Aim-Height')end;local bs,bt,bu=predict_cords(f)local b1,b2,b3=render.world_to_screen(bp-M,bq-N,br-O)local b4,b5,b6=render.world_to_screen(bs-M,bt-N,bu-O)if b3<1 and b6<1 then render.line(b1,b2,b4,b5,3,color[1],color[2],color[3],color[4])end end end;if module_manager.option('AutoProjectile','Render','Thrown-ESP')then for ab=1,#j,1 do local aZ,a_,b0=j[ab]['x'],j[ab]['y'],j[ab]['z']local b1,b2,b3=render.world_to_screen(aZ-M,a_-N,b0-O)local b4,b5,b6=render.world_to_screen(aZ-M+0.25,a_-N,b0-O)local b7,b8,b9=render.world_to_screen(aZ-M,a_-N,b0-O+0.25)local ba,bb,bc=render.world_to_screen(aZ-M-0.25,a_-N,b0-O)local bd,be,bf=render.world_to_screen(aZ-M,a_-N,b0-O-0.25)local bg,bh,bi=render.world_to_screen(aZ-M,a_-N+0.25,b0-O)local bj,bk,bl=render.world_to_screen(aZ-M,a_-N-0.25,b0-O)if b3<1 and b6<1 and b9<1 and bc<1 and bf<1 and bi<1 and bl<1 then local color=module_manager.option('AutoProjectile','Render','Thrown-Color')render.line(b1,b2,b4,b5,3,color[1],color[2],color[3],color[4])render.line(b1,b2,b7,b8,3,color[1],color[2],color[3],color[4])render.line(b1,b2,ba,bb,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bd,be,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bg,bh,3,color[1],color[2],color[3],color[4])render.line(b1,b2,bj,bk,3,color[1],color[2],color[3],color[4])end end end;render.scale(aY.scale_factor)end,on_enable=function()m=player.held_item_slot()c,d,e=1,0,0;h,k=false,false;l=player.held_item_slot()end,on_disable=function()if l and m and l~=m then o=true;player.set_held_item_slot(m-2)o=false end;if module_manager.is_module_on('Auto-Rod')then player.message('.auto-rod')end end,on_send_packet=function(bv)if bv.packet_id==0x09 then l=bv.slot+1;if not o then m=l end end;return bv end}module_manager.register('dajiudwuiha',aX)"
                        ),
                        new Scaffold(Category.PLAYER),
                        new AutoRespawn(Category.PLAYER),
                        new Timer(Category.PLAYER),
                        new NoRotate(Category.PLAYER),
                        new FastMine(Category.PLAYER),
                        new DoubleJump(Category.MOVEMENT),
                        new Stealer(Category.PLAYER),
                        new Freecam(Category.PLAYER),
                        new MiddleClickFriends(),
                        new Phase(Category.PLAYER),
                        new InventoryManager(Category.PLAYER),
                        new Blink(Category.PLAYER),
                        new Fucker(Category.PLAYER),
                        new Step(Category.PLAYER),
                        new FastUse(Category.PLAYER),
                        new AutoTool(Category.PLAYER),
                        new AutoFish(Category.PLAYER),
                        new SpeedBridge(Category.PLAYER),
                        new NoFall(Category.MOVEMENT),
                        new Sprint(Category.MOVEMENT),
                        ModuleManager.D = new NoSlow(Category.MOVEMENT),
                        new Flight(Category.MOVEMENT),
                        new Speed(Category.MOVEMENT),
                        ModuleManager.r = new Inventory(Category.MOVEMENT),
                        new Jesus(Category.MOVEMENT),
                        new AntiFall(Category.MOVEMENT),
                        new Terrain(Category.MOVEMENT),
                        new Sneak(Category.MOVEMENT),
                        new LongJump(Category.MOVEMENT),
                        new SafeWalk(Category.MOVEMENT),
                        new Water(Category.MOVEMENT),
                        new LegitScaffold(Category.MOVEMENT, var139),
                        new PingSpoofer(Category.EXPLOITS),
                        new HypixelPacketHandler(),
                        new ChatFilter(Category.EXPLOITS),
                        new AntiHunger(Category.EXPLOITS),
                        new ConsoleKiller(Category.EXPLOITS),
                        new RogueSign(Category.EXPLOITS),
                        new Disabler(
                           Category.EXPLOITS,
                           var141,
                           "local a={}local b={}local c=0;local d=false;local e=0;local f,g=0,0;local h=0;local i=0;local j=0;local k=false;local l=false;local m=false;local n=false;local o=0;local function p()do return false end;for q=44,9,-1 do local r,s,t,u,v=player.inventory.item_information(q)if r~=nil and string.match(r,\"compass\")then return true end end;return false end;local function w()if player.ticks_existed()>35 and not p()and false then return true end;return false end;local function x(y)return y.packet_id==0x03 or y.packet_id==0x04 or y.packet_id==0x05 or y.packet_id==0x06 end;local function z(A,B,C)local D,E,F=player.position()return math.sqrt((D-A)^2+(E-B)^2+(F-C)^2)end;local function G(H,I)if I~=nil then while#b>I do if b[1]~=nil then player.send_packet_no_event(0x00,b[1])end;table.remove(b,1)end end;if H~=nil then while#a>H do if a[1]~=nil then player.send_packet_no_event(0x0F,a[1][1],a[1][2],a[1][3])end;table.remove(a,1)end end end;module_manager.register(\"gytffty\",{on_enable=function()e=0;c=client.time()d=false end,on_disable=function()G(0,0)world.set_timer(1)end,on_player_join=function()a={}b={}c=0;d=false;e=0;f,g=0,0;h=0;i=0;j=0;k=false;l=false;m=false;n=false;o=0 end,on_send_packet=function(J)if w()then if J.packet_id==0x0F then J.cancel=true;table.insert(a,{J.window_id,J.uid,J.accept})if not d then d=true end end;if J.packet_id==0x00 then J.cancel=true;table.insert(b,J.keep_alive_key)if n then c=client.time()n=false end end;if J.packet_id==0x02 and J.action==2 then j=client.time()end end;if J.packet_id==0x03 or J.packet_id==0x05 then J.cancel=true end;if J.packet_id==0x0B or J.packet_id==0x02 then if not d and w()or player.ticks_existed()<=35 then J.cancel=true end end;if(J.packet_id==0x0A or J.packet_id==0x08)and player.ticks_existed()<=35 then J.cancel=true end;if player.ticks_existed()<=35 then if J.packet_id==0x06 or J.packet_id==0x05 or J.packet_id==0x04 then if J.packet_id==0x06 or player.ticks_existed()%2==0 then J.cancel=true end end end;return J end,on_receive_packet=function(J)if w()then if J.packet_id==0x02 and l then if J.message:find(\"command. Type\")then J.cancel=true;h=client.time()-i;l=false end end end;if J.packet_id==0x08 then if player.ticks_existed()<=35 and z(J.pos_x,J.pos_y,J.pos_z)<=10 then J.cancel=true;if not m then player.send_packet_no_event(0x04,J.pos_x,J.pos_y+math.random()/1250+math.random()/750,J.pos_z,false)m=true else if player.ticks_existed()>35 then player.send_packet_no_event(0x04,J.pos_x,J.pos_y,J.pos_z,false)end end;player.set_position(J.pos_x,J.pos_y,J.pos_z)player.set_motion(0,-0.0784,0)end end;return J end,on_pre_motion=function(J)if w()then if client.time()-i>=10000 and client.time()-j>=350 then player.message(\"/\"..tostring(math.random()))i=client.time()l=true end;f=math.max(0,400-h)g=math.max(200,600-h)if d then if client.time()-c>=e then if#a>0 or#b>0 then G(nil,0)e=math.random(f,g)n=true;o=client.time()c=client.time()end end;if client.time()-o>=e/2 then G(0,nil)o=client.time()*2 end end end;return J end})"
                        ),
                        new SuicideClip(Category.EXPLOITS),
                        new Plugins(Category.EXPLOITS),
                        new HideWindow(Category.EXPLOITS),
                        new PackSpoofer(Category.OTHER),
                        new AntiTabComplete(Category.OTHER),
                        new Annoy(Category.OTHER),
                        new FriendProtect(Category.OTHER),
                        new Announcer(Category.OTHER),
                        new AutoHypixel(Category.OTHER),
                        new ServerRotationTracker(),
                        new WorldTime(Category.OTHER),
                        new AntiFrozen(Category.OTHER),
                        new FastPlace(Category.OTHER),
                        new KillSay(Category.OTHER),
                        new Spammer(Category.OTHER),
                        new AntiDesync(Category.OTHER),
                        ModuleManager.B = new AntiObsidian(Category.OTHER),
                        ModuleManager.Z = new InfiniteReach(Category.OTHER),
                        new ThunderNotifier(Category.OTHER),
                        ModuleManager.Q = new NoMouseIntersect(Category.OTHER),
                        new HackerDetector(Category.OTHER),
                        new FreeLook(Category.OTHER),
                        new GUI(Category.HIDDEN),
                        new ESP(Category.VISUALS),
                        new Chams(Category.VISUALS),
                        new HUD(Category.VISUALS),
                        new FullBright(Category.VISUALS),
                        new Waypoints(Category.VISUALS),
                        ModuleManager.X = new NoEffect(Category.VISUALS),
                        ModuleManager.i = new Scoreboard(Category.VISUALS),
                        new Skeleton(Category.VISUALS),
                        ModuleManager.k = new Camera(Category.VISUALS),
                        new Outline(Category.VISUALS),
                        new Animations(Category.VISUALS),
                        new Tracers(Category.VISUALS),
                        new Crosshair(Category.VISUALS),
                        new Radar(Category.VISUALS),
                        ModuleManager.t = new CaveFinder(Category.VISUALS),
                        ModuleManager.I = new NoRender(Category.VISUALS),
                        new ChunkBorders(Category.VISUALS),
                        new TitleAnnouncer(),
                        ModuleManager.S = new NoTitle(Category.VISUALS),
                        new Trajectories(Category.VISUALS),
                        new NameProtect(Category.VISUALS),
                        ModuleManager.G = new StreamerMode(Category.VISUALS),
                        ModuleManager.s = new Xray(Category.VISUALS),
                        new BlockOverlay(Category.VISUALS),
                        new DamageParticles(Category.VISUALS),
                        ModuleManager.M = new WorldColor(Category.VISUALS),
                        ModuleManager.J = new ItemPhysics(Category.VISUALS),
                        new PlayerList(Category.VISUALS),
                        ModuleManager.N = new MoreParticles(Category.VISUALS),
                        new RiderGod(Category.EXPLOITS),
                        new Nuker(Category.PLAYER)
                     );
                  ClientAccess.client
                     .getCommandManager()
                     .add(
                        new BindCommand("bind", "add <module> <key>/del <module>/clear"),
                        new WaypointsCommand("waypoints", "add <name>/remove <name>"),
                        new NameCommand("name", "Copies your username to clipboard"),
                        new SettingsCommand("settings", "load <name>/save <name>"),
                        new VclipCommand("vclip", "<distance>"),
                        new FriendsCommand("friends", "add <name> <alias>/del <name/alias>/clear"),
                        new JumpCommand("jump", "junps"),
                        new EnemiesCommand("enemies", "add <name> <alias>/del <name/alias>/clear"),
                        new HelpCommand("help", "Help"),
                        new ClearchatCommand("clearchat", "Clears chat"),
                        new ScriptCommand("script", "<list/unsafe/reload/name enable/name disable>"),
                        new HclipCommand("hclip", "Clips horizontally"),
                        new NicklistCommand("nicklist", "Shows all the found nicked players in hypixel by using the denicker module"),
                        new IteminfoCommand("iteminfo", "iteminfo <player name>"),
                        new HideCommand("hide", "Hide in array <module/all>"),
                        new ShowCommand("show", "Show in array <module/all>"),
                        new FinalsCommand("finals", "Shows players with finals kills in Hypixel Mega Walls"),
                        new ChatCommand("chat", "<enabled/pings>"),
                        new FontCommand("font", "Change the client font"),
                        new BansCommand("bans", "sw/bw/uhc/mw/all/pit <hours>"),
                        new TeleportCommand(
                           "teleport",
                           "<x> <y> <z>/<x> <z>",
                           "local a=1;local b,c,d=0,0,0;local e,f,g=0,0,0;local h,i,j;local k,l,m,n,o=0,0,0,0,0;local p,q,r,s,t;local u=-1;local v=247;local w=2000;local x=500;local y={on_send_packet=function(z)if z.packet_id==0x01 then local A={}for B in string.gmatch(z.message,'%S+')do table.insert(A,B)end;if A[1]=='!tp'and#A==4 then z.cancel=true;h,i,j=math.floor(tonumber(A[2])+0.5),math.floor(tonumber(A[3])),math.floor(tonumber(A[4])+0.5)e,f,g=player.position()f=v;player.message('/l')a=2 elseif A[1]=='!tp'and#A==3 then z.cancel=true;h,i,j=math.floor(tonumber(A[2])+0.5),v,math.floor(tonumber(A[3])+0.5)e,f,g=player.position()f=v;player.message('/l')a=2 elseif A[1]=='!tp'and#A==2 then z.cancel=true;if A[2]=='back'then h,i,j=math.floor(e)+0.5,f,math.floor(g)+0.5;e,f,g=player.position()f=v;player.message('/l')a=2 else local C=world.entities()for D=1,#C,1 do local E=C[D]if world.name(E)==A[2]then h,i,j=world.position(E)h,j=math.floor(h+0.5),math.floor(j+0.5)i=v;e,f,g=player.position()f=v;player.message('/l')a=2 end end end end elseif a==4 and(z.packet_id==0x03 or z.packet_id==0x04 or z.packet_id==0x05 or z.packet_id==0x06)then z.cancel=true end;return z end,on_player_join=function()if a==2 then client.print('will attempt to teleport to ('..h..', '..i..', '..j..')')client.print('sneak to cancel')player.message('/rej')a=3 end;if a==3 then l,m=client.time(),client.time()b,c,d=0,0,0;a=4 end;o=client.time()end,on_pre_update=function()if a==1 and input.is_key_down(26)and input.is_mouse_down(2)and client.time()-k>1000 then local F,G=player.angles()local H,I,J,K,L,M,N,O=player.ray_cast(F,G,500)if H==2 then h,i,j=math.floor(J)+0.5,v,math.floor(L)+0.5;k=client.time()player.message('/l')a=2 end end;if a==4 then local F,G=player.angles()player.send_packet_no_event(0x06,h,i,j,F,G,false)player.send_packet_no_event(0x03,false)player.send_packet_no_event(0x06,h,i,j,F,G,false)player.send_packet_no_event(0x03,false)b=b+1;if c>0 and client.time()-o>w and client.time()-m>x then client.print('teleported')player.set_position(h,i,j)u=10;b,c,d=0,0,0;a=1 end;if d>0 and client.time()-o>w and client.time()-l>x then client.print('failed to teleport')u=10;b,c,d=0,0,0;a=1 end;if input.is_key_down(42)then client.print('canceled on sneak')u=10;b,c,d=0,0,0;a=1 end end;if u==0 then world.refresh_chunks()end;u=u-1 end,on_receive_packet=function(z)if z.packet_id==0x08 then p=z.pos_x;q=z.pos_y;r=z.pos_z;s=z.yaw;t=z.pitch;n=client.time()end;if z.packet_id==0x08 and a==4 and math.floor(z.pos_x)==math.floor(h)and math.floor(z.pos_z)==math.floor(j)then player.set_position(h,i,j)c=c+1;d=0;l=client.time()elseif a==4 then d=d+1;c=0;m=client.time()end;return z end}module_manager.register('draaaven',y)"
                        ),
                        new LimboCommand("limbo", "Sends you to limbo in Hypixel")
                     );
                  String var143 = "1,8,8+220801-2a1585a864d9e67627c6ae04c807a2c5/c35312fb3a7e05b7a44db2326bd29040/37f62f1363b04df4370753037853fe88/KillAura/1d123b3c5603b87fd84e1a48ba0bed6c/Velocity/72ca5c93acd491a7a757ed28483ffce8/Criticals/MzIzMjMy/AutoClicker/H/AutoArmor/P/Regen/AutoHeal/FastBow/AutoBackstab/Reach/AimAssist/Knockback/Hitboxes/AntiFireball/KeepSprint/AutoProjectile/Scaffold/AutoRespawn/Timer/NoRotate/FastMine/DoubleJump/Stealer/Freecam/M/Phase/InventoryManager/Blink/Fucker/Step/FastUse/AutoTool/AutoFish/SpeedBridge/NoFall/Sprint/NoSlow/Flight/Speed/Inventory/Jesus/AntiFall/Terrain/Sneak/LongJump/SafeWalk/Water/PingSpoofer/SEX/ChatFilter/AntiHunger/ConsoleKiller/RogueSign/Disabler/SuicideClip/Plugins/HideWindow/PackSpoofer/AntiTabComplete/Annoy/FriendProtect/Announcer/AutoHypixel/PP/WorldTime/AntiFrozen/FastPlace/KillSay/Spammer/AntiDesync/AntiObsidian/InfiniteReach/ThunderNotifier/NoMouseIntersect/HackerDetector/FreeLook/GUI/ESP/Chams/HUD/FullBright/Waypoints/NoEffect/Scoreboard/Skeleton/Camera/Outline/Animations/Tracers/Crosshair/Radar/CaveFinder/NoRender/ChunkBorders/Sex1/NoTitle/Trajectories/NameProtect/StreamerMode/Xray/BlockOverlay/DamageParticles/WorldColor/ItemPhysics/PlayerList/MoreParticles/RiderGod/Nuker";
                  String[] var145 = var143.split(";");
                  if (!var143.isEmpty()) {
                     String[] var146 = var143.split("/");

                     for (int var28 = 0; var28 < var146.length; var28++) {
                        String var29 = var146[var28];
                        if (ClientAccess.client.getModuleManager().getList().get(var28) instanceof Module) {
                           Module var30 = (Module)ClientAccess.client.getModuleManager().getList().get(var28);
                           if (var30.getName() == null) {
                              var30.setName(var29);
                           }

                           if (var30.getDisplayName() == null) {
                              var30.setDisplayName(var30.getName());
                           }
                        }
                     }
                  }

                  ConfigFiles.load("default");
                  ConfigFiles.save(ConfigFiles.serializeModules(), "default");
                  byte var147 = 3;
                  NumberSetting var148 = null;

                  for (Category var32 : Category.values()) {
                     if (var32 != Category.HIDDEN && var32 != Category.CONFIG) {
                        CategoryPanel var33 = new CategoryPanel(var32, var147, 3, 107, 17);

                        for (Module var35 : Astolfo.INSTANCE.getModuleManager().getModules(var32)) {
                           ModuleButton var36 = new ModuleButton(var33, var35, 103, 15);

                           for (Setting var38 : var35.getSettings()) {
                              if (!var38.getName().startsWith("Pos-") && !var38.getName().equals("Hide")) {
                                 if (var38 instanceof ColorSetting) {
                                    var36.M_ArrayList().add(new ColorPicker(var32, var36, var38, 99, 70));
                                 }

                                 if (var38 instanceof BooleanSetting) {
                                    var36.M_ArrayList().add(new BooleanComponent(var32, var36, var38, 99, 10));
                                 }

                                 if (var38 instanceof NumberSetting) {
                                    if (var38.getName().startsWith("Min-") && !var38.getName().contains("Min-Health")) {
                                       var148 = (NumberSetting)var38;
                                       continue;
                                    }

                                    if (var38.getName().startsWith("Max-") && var148 != null) {
                                       var36.M_ArrayList().add(new RangeSlider(var32, var36, var148, var38, 99, 10));
                                       var148 = null;
                                       continue;
                                    }

                                    var36.M_ArrayList().add(new NumberSlider(var32, var36, var38, 99, 10));
                                 }

                                 if (var38 instanceof EnumSetting) {
                                    var36.M_ArrayList().add(new EnumComponent(var36, var38, 99, 10));
                                 }

                                 if (var38 instanceof StringSetting) {
                                    var36.M_ArrayList().add(new StringComponent(var32, var36, var38, 93, 10));
                                 }

                                 if (var38 instanceof MapSetting) {
                                    MapComponent var39 = new MapComponent(var36, var38, 99, 10);

                                    for (Setting var41 : ((MapSetting)var38).getSettings().values()) {
                                       if (!var41.getName().startsWith("Pos-")) {
                                          if (var41 instanceof BooleanSetting) {
                                             var39.M_ArrayList().add(new BooleanComponent(var32, var39, var41, 93, 10));
                                          }

                                          if (var41 instanceof NumberSetting) {
                                             if (var41.getName().startsWith("Min-") && !var38.getName().contains("Min-Health")) {
                                                var148 = (NumberSetting)var41;
                                                continue;
                                             }

                                             if (var41.getName().startsWith("Max-")) {
                                                var39.M_ArrayList().add(new RangeSlider(var32, var39, var148, var41, 93, 10));
                                                var148 = null;
                                                continue;
                                             }

                                             var39.M_ArrayList().add(new NumberSlider(var32, var39, var41, 93, 10));
                                          }

                                          if (var41 instanceof EnumSetting) {
                                             var39.M_ArrayList().add(new EnumComponent(var39, var41, 93, 10));
                                          }

                                          if (var41 instanceof StringSetting) {
                                             var39.M_ArrayList().add(new StringComponent(var32, var39, var41, 93, 10));
                                          }

                                          if (var41 instanceof ColorSetting) {
                                             var39.M_ArrayList().add(new ColorPicker(var32, var39, var41, 93, 70));
                                          }
                                       }
                                    }

                                    var36.M_ArrayList().add(var39);
                                 }
                              }
                           }

                           var33.a(var36);
                        }

                        ClientAccess.client.getDefaultPanelManager().addElements(var33);
                        var147 += 125;
                     }
                  }

                  byte var150 = 3;

                  for (Category var156 : Category.values()) {
                     if (var156 != Category.HIDDEN && var156 != Category.CONFIG) {
                        bypass.gui.clickgui.transparent.CategoryPanel var157 = new bypass.gui.clickgui.transparent.CategoryPanel(var156, var150, 3, 105, 10);

                        for (Module var159 : Astolfo.INSTANCE.getModuleManager().getModules(var156)) {
                           bypass.gui.clickgui.transparent.ModuleButton var160 = new bypass.gui.clickgui.transparent.ModuleButton(var157, var159, 103, 10);

                           for (Setting var162 : var159.getSettings()) {
                              if (!var162.getName().startsWith("Pos-") && !var162.getName().equals("Hide")) {
                                 if (var162 instanceof BooleanSetting) {
                                    var160.M_ArrayList().add(new bypass.gui.clickgui.transparent.BooleanComponent(var160, var162, 101, 10));
                                 }

                                 if (var162 instanceof NumberSetting) {
                                    var160.M_ArrayList().add(new bypass.gui.clickgui.transparent.NumberSlider(var160, var162, 101, 10));
                                 }

                                 if (var162 instanceof EnumSetting) {
                                    var160.M_ArrayList().add(new bypass.gui.clickgui.transparent.EnumComponent(var160, var162, 101, 10));
                                 }

                                 if (var162 instanceof StringSetting) {
                                    var160.M_ArrayList().add(new bypass.gui.clickgui.transparent.StringComponent(var160, var162, 101, 10));
                                 }

                                 if (var162 instanceof MapSetting) {
                                    bypass.gui.clickgui.transparent.MapComponent var163 = new bypass.gui.clickgui.transparent.MapComponent(
                                       var160, var162, 101, 10
                                    );

                                    for (Setting var42 : ((MapSetting)var162).getSettings().values()) {
                                       if (!var42.getName().startsWith("Pos-")) {
                                          if (var42 instanceof BooleanSetting) {
                                             var163.M_ArrayList().add(new bypass.gui.clickgui.transparent.BooleanComponent(var163, var42, 98, 10));
                                          }

                                          if (var42 instanceof NumberSetting) {
                                             var163.M_ArrayList().add(new bypass.gui.clickgui.transparent.NumberSlider(var163, var42, 98, 10));
                                          }

                                          if (var42 instanceof EnumSetting) {
                                             var163.M_ArrayList().add(new bypass.gui.clickgui.transparent.EnumComponent(var163, var42, 98, 10));
                                          }

                                          if (var42 instanceof StringSetting) {
                                             var163.M_ArrayList().add(new bypass.gui.clickgui.transparent.StringComponent(var163, var42, 98, 10));
                                          }
                                       }
                                    }

                                    var160.M_ArrayList().add(var163);
                                 }
                              }
                           }

                           var157.a(var160);
                        }

                        Astolfo.INSTANCE.getTransparentPanelManager().addElements(var157);
                        var150 += 110;
                     }
                  }

                  if (Disabler.jsHandler instanceof ScriptObjectMirror) {
                     ScriptObjectMirror var153 = (ScriptObjectMirror)Disabler.jsHandler;
                     var153.put("dsa", this.p);
                     var153.call("init", new Object[0]);
                  }

                  System.gc();
               }
            }

            var19.E(this.Q);
         }

         this.o(this.Q);
      }

      return ReceiveResult.B;
   }

   public static String N(String var0) {
      return D(var0, "4N4z6CCb6bvBeeJH");
   }

   public static String D(String var0, String var1) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            SecretKeySpec var5 = new SecretKeySpec(var1.getBytes(), "AES");
            Cipher var9 = Cipher.getInstance("AES/ECB/PKCS5Padding");
            var9.init(2, var5);
            byte[] var7 = var9.doFinal(DatatypeConverter.parseBase64Binary(var0));
            return new String(var7);
         } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException var8) {
            NullPointerException var6 = new NullPointerException();
            var6.setStackTrace(new StackTraceElement[0]);
            throw var6;
         }
      } else {
         return var0;
      }
   }

   private ReceiveResult R() throws IOException {
      this.v = true;

      try {
         if (this.W.read(this.V) == -1) {
            this.v = false;
            this.e = -1;
            return ReceiveResult.C;
         }
      } catch (IOException var5) {
         this.v = false;
         this.e = -1;
         throw var5;
      }

      if (this.V.position() != this.V.capacity()) {
         return ReceiveResult.A;
      }

      ((Buffer)this.V).flip();
      this.p = XorMasks.m(this.V.get());
      this.e = (short)(this.V.getShort() ^ this.p);
      this.z = (short)(this.V.getShort() ^ this.p);
      if (this.e == 0) {
         this.v = false;
         this.e = 0;
      } else {
         this.r = this.g.get(this.z);
      }

      ((Buffer)this.V).clear();
      this.E = 0;
      this.Q = null;
      return ReceiveResult.B;
   }

   public boolean M() {
      return this.v;
   }
}
