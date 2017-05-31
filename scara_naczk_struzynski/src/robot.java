//=========================================================
//|                   PROJEKT Z POiGK                     |
//=========================================================

import java.applet.Applet;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;


public class robot extends Applet implements ActionListener, KeyListener {
 //=============================================================
 //  trancformacje obiektów
 //===========================================================


 private TransformGroup pozycja;
 private TransformGroup pozycjaRamienia;//wskazuje czubek
 private TransformGroup rotacjaRamienia;
 private TransformGroup pozycjaPrzedramienia;//wskazuje czubek
 private TransformGroup rotacjaPrzedramienia;
 private TransformGroup przesuniecieWału_GD;//wskazuje czubek
 private TransformGroup pozycjaWału;
 private TransformGroup Alfa_Body_Rotation_1;
 private TransformGroup pret_; //wskazuje koniec preta
 private TransformGroup pudelko; //wskazuje koniec preta


 
 private Button przyciskStart = new Button("Start");   //Uruchomienie robota
 private Button przyciskStop = new Button("Stop"); //opis sterowania
 private Transform3D trans = new Transform3D();
 private Transform3D rotacja_1 = new Transform3D();
 private Transform3D rotacja_2 = new Transform3D();
 private Transform3D przesuniecie_3 = new Transform3D();
 private Transform3D przesuniecie_pret_ = new Transform3D();
 private Transform3D przesuniecie_pudla = new Transform3D();

 //private TransformGroup objTrans;
 private float height=0.0f;
 private float sign=1.0f;
 private Timer zegar_1;
 private Timer zegar_2;
 private float xloc=0.0f;
 private float yloc=0.0f;
 private double kat_1=0,kat_2=0;
 private float przes_3=0;
 private boolean klawisz_a=false, klawisz_s=false, klawisz_d=false, pierwsza_spacja = true;
 private boolean klawisz_w=false, klawisz_q=false, klawisz_e=false, klawisz_space = false, w_pudle=false, nagrywanie=false, odtwarzanie=false;

 //niesprawdzone
 private boolean podniesiony=false, spada=false;
 Cylinder pret = null;
 Box pudlo = null;
 BranchGroup wezel_scena = null;
 BranchGroup scena = null;
 BranchGroup  branch_pudlo = null;
 float fallCounter=0;
 Queue<Integer> ruch = new LinkedList<Integer>();

 
 public robot() {
       
       setLayout(new BorderLayout());
       GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //odczyt konfiguracji
       Canvas3D canvas3D = new Canvas3D(config);
       add("Center",canvas3D);
       canvas3D.addKeyListener(this);
       zegar_1 = new Timer(10,this);     // częstość odświerzania pozycji
       zegar_2 = new Timer(1000,this);     // częstość odczytywania klawiszy

       JPanel panel = new JPanel();      // Jpanel utworzony w górnej części okna
       panel.add(przyciskStart);
       panel.add(przyciskStop);
       add("" + "North",panel);          // lokalizacja panela
       przyciskStart.addActionListener(this);
       przyciskStart.addKeyListener(this);

       scena = utworzScene();    //główna scena
       scena.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE); //ustawienie uprawnień do dodawaia dzieci
        scena.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        scena.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	    scena.compile();
       SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

       BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0),100);

       //=============================================================================
       //  Obserwator
       //=============================================================================
        
       //sterowanie obserwatorem
       OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL); //sterowanie myszką
       orbit.setSchedulingBounds(bounds);
       ViewingPlatform vp = simpleU.getViewingPlatform();
       vp.setViewPlatformBehavior(orbit);
       //przesunięcie obserwatora
       Transform3D przesuniecie_obserwatora = new Transform3D();
       Transform3D rot_obs = new Transform3D();
       //rot_obs.rotY((float)(-Math.PI/7));

        przesuniecie_obserwatora.set(new Vector3f(0.0f,0.5f,3.0f)); //odległość obserwatora (-1.2f,1.5f,2.0f
        //przesuniecie_obserwatora.mul(rot_obs);
        //rot_obs.rotX((float)(-Math.PI/6));
        //przesuniecie_obserwatora.mul(rot_obs);

        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        simpleU.addBranchGraph(scena);
    }
//przyciski sterujące robotem
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar()=='d') {klawisz_d=true;}
        if (e.getKeyChar()=='a') {klawisz_a=true;}
        if (e.getKeyChar()=='w') {klawisz_w=true;}
        if (e.getKeyChar()=='s') {klawisz_s=true;}
        if (e.getKeyChar()=='q') {klawisz_q=true;}
        if (e.getKeyChar()=='e') {klawisz_e=true;}
        if (e.getKeyChar()==' ') {klawisz_space=true;}
        if (e.getKeyChar()=='r') {nagrywanie=true; odtwarzanie=false;}
        if (e.getKeyChar()=='p') {nagrywanie=false; odtwarzanie=true;}
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar()=='a') {klawisz_a=false;}
        if (e.getKeyChar()=='d') {klawisz_d=false;}
        if (e.getKeyChar()=='w') {klawisz_w=false;}
        if (e.getKeyChar()=='s') {klawisz_s=false;}
        if (e.getKeyChar()=='q') {klawisz_q=false;}
        if (e.getKeyChar()=='e') {klawisz_e=false;}
        if (e.getKeyChar()==' ') {klawisz_space=false; pierwsza_spacja=true;}
    }
    public void keyTyped(KeyEvent e) {}


    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==przyciskStart)
        {
            if(!zegar_1.isRunning()) {zegar_1.start();} //jeżeli naciśnięto "start" - uruchomienie zegara 1
        } else {
            height += .01*sign;
            if(Math.abs(height*2)>=1)sign=-1.0f*sign;
            if(height<-0.4f) {
                trans.setScale(new Vector3d(1.0,1.0,1.0));
            }

            //trans.setTranslation(new Vector3f(xloc, height, yloc));
            //objTrans.setTransform(trans);

         //=================================
         //  obsługa klawiszy
         //=================================
         
         //sprawdzanie czy koncowka jest w pudelku
            //koncowka preta
          Transform3D transform = new Transform3D();
          Vector3f position = new Vector3f();
          pret.getLocalToVworld(transform); 
          transform.get(position);
          
          //lokalizacja pudelka
          Transform3D transformObject = new Transform3D(); 
          Vector3f positionObject = new Vector3f();
          pudlo.getLocalToVworld(transformObject); 
          transformObject.get(positionObject);
          
          if(position.x>positionObject.x-0.1f  &&   position.x<positionObject.x+0.1f  &&   position.z>positionObject.z-0.1f   &&   position.z<positionObject.z+0.1f   &&   position.y<=0.5)
          {
              w_pudle=true;
          }else w_pudle=false;
         
         
            if(kat_1 > -2.65){
                if(klawisz_d==true && w_pudle==false)
                {
                    kat_1=kat_1-0.03;
                    if(nagrywanie) ruch.add(1);
                }
            }
            if(kat_1 < 2.65){
                if(klawisz_a==true && w_pudle==false)
                {
                    kat_1=kat_1+0.03;
                    if(nagrywanie) ruch.add(2);
                }
            }
            if(kat_2 > -2.65){
                if(klawisz_e==true && w_pudle==false)
                {
                    kat_2=kat_2-0.04;
                    if(nagrywanie) ruch.add(3);
                }
            }
            if(kat_2 < 2.65){
                if(klawisz_q==true && w_pudle==false)
                {
                    kat_2=kat_2+0.04;
                    if(nagrywanie) ruch.add(4);
                }
            }
                
            if(w_pudle)
            {
                if(-0.16f < przes_3)
                {
                if(klawisz_s==true)
                {
                    przes_3=przes_3-0.005f;
                    if(nagrywanie) ruch.add(5);
                }
                }
            }else
            {
                if(-0.3f < przes_3)
                {
                if(klawisz_s==true)
                {
                    przes_3=przes_3-0.005f;
                    if(nagrywanie) ruch.add(5);
                }
                }
            }
            
            
            if(przes_3 < 0.3f){
                if(klawisz_w==true)
                {
                    przes_3=przes_3+0.005f;
                    if(nagrywanie) ruch.add(6);
                }
            }
            if(klawisz_space)
            {
                if(pierwsza_spacja)
                {
                    pierwsza_spacja=false;
                    if(nagrywanie) ruch.add(7);
                    System.out.println("wcisnieta spacja");
                    podnies();
                }
            }
            
            //jesli będzie otwarzanie
            if(odtwarzanie)
            {
                if(!ruch.isEmpty()){ //kolejka zapisu ruchu
                    switch (ruch.poll()){
                   case 1:
                       kat_1 = kat_1-0.03;
                       break;
                   case 2:
                       kat_1 = kat_1+0.03;
                       break;
                   case 3:
                       kat_2 = kat_2-0.04;
                       break;
                   case 4:
                       kat_2 = kat_2+0.04;
                       break;
                   case 5:
                       przes_3 = przes_3 - 0.005f;
                       break;
                   case 6:
                        przes_3 = przes_3 + 0.005f;
                       break;
                    case 7:
                       podnies();
                       break;
                   default:
                       break;
               }
                }else
                 {
                    odtwarzanie=false;
                 }
            }
            
            rotacja_1.rotY(kat_1);
            rotacjaRamienia.setTransform(rotacja_1);

            rotacja_2.rotY(kat_2);
            rotacjaPrzedramienia.setTransform(rotacja_2);

            przesuniecie_3.setTranslation(new Vector3f(0f, przes_3,0f));
            pozycjaWału.setTransform(przesuniecie_3);
            
            //========================
              //    Grawitacja
            //======================
            if(spada){ //spadek
            System.out.println("spadaaaaam");
            
            Transform3D transformObject_spad = new Transform3D();
            pudlo.getLocalToVworld(transformObject_spad);
             Vector3f position_spad = new Vector3f();
             
              transformObject_spad.get(position_spad);
       
        position_spad.setY(-0.01f);
        position_spad.setX(0);
        position_spad.setZ(0);
        
        
      //  position_spad.y= 0.8f;
        Transform3D trans = new Transform3D();
        trans.set(position_spad);
        //transform.set(position);
        przesuniecie_pudla.mul(transformObject_spad, trans);   //przesunięcie odłączonego obiektu w nowe miejsce
        pudelko.setTransform(przesuniecie_pudla);
        //do pomiaru
        Transform3D transformPomiar = new Transform3D(); 
            Vector3f positionPomiar = new Vector3f();
            pudlo.getLocalToVworld(transformPomiar); 
            transformPomiar.get(positionPomiar);
            System.out.println("wysokosc1: "+positionPomiar.getY());
            if(positionPomiar.y<=0.08f) spada=false;
            /*
            Transform3D transformObject_spad = new Transform3D(); 
            Vector3f positionObject_spad = new Vector3f();
            pudlo.getLocalToVworld(transformObject_spad); 
            transformObject_spad.get(positionObject_spad);
            if(positionObject_spad.y<=-0.2f) spada=false; //spadek póki współrzędna nie osiągnie progu
            */
       }
            
        }
    } //KONIEC actionPerformed
    //========================
    //    podnoszenie klocka
    //======================
    void podnies()
    {
      if(!podniesiony)
      {
                              System.out.println("jestem nie podniesiony");

          //koncowka preta
          Transform3D transform = new Transform3D();
          Vector3f position = new Vector3f();
          pret.getLocalToVworld(transform); 
          transform.get(position);
          
          //lokalizacja pudelka
          Transform3D transformObject = new Transform3D(); 
          Vector3f positionObject = new Vector3f();
          pudlo.getLocalToVworld(transformObject); 
          transformObject.get(positionObject);
          
          //sprawdzanie wysokości
          
          
        //sprawdzanie warunku podniesienia
            if(position.x>positionObject.x-0.1f  &&   position.x<positionObject.x+0.1f  &&   position.z>positionObject.z-0.1f   &&   position.z<positionObject.z+0.1f   &&   position.y<=0.5)
            {
                System.out.println("trafiłem huja");
                podniesiony=true;
                przesuniecie_pudla.set(new Vector3f(0,-0.4f,0));
               pudelko.setTransform(przesuniecie_pudla);
                //tu może się jebnąć bo nie usuwam pudelkobranch tylko trasformfroup pudelka
                
                scena.removeChild(branch_pudlo);
                pret_.addChild(branch_pudlo); //dodanie obiektu do chwytaka
                spada=false;
            }   
          
      }else
      {
          podniesiony=false;
          Transform3D transform = new Transform3D(); 
        Vector3f position = new Vector3f();

        Transform3D tempRot = new Transform3D();
        tempRot.rotY(kat_2);
        
        pudlo.getLocalToVworld(transform); //sprawdzenie pozycji obiektu
        transform.get(position);
        pret_.removeChild(branch_pudlo);
        scena.addChild(branch_pudlo);
        position.setY(-0);
        position.setX(0);
        position.setZ(0);
        Transform3D trans = new Transform3D();
        trans.set(position);
        //transform.set(position);
        przesuniecie_pret_.mul(transform, trans);   //przesunięcie odłączonego obiektu w nowe miejsce
        pudelko.setTransform(przesuniecie_pret_);
        
        spada=true;
      }
      
        
        
    }//koniec podnies
  
    //========================
    //    WYGLĄD ROBOTA
    //======================
    public BranchGroup utworzScene() {

        wezel_scena = new BranchGroup();
        Appearance wyglad_ziemia = new Appearance();
        Appearance wyglad_ramie = new Appearance();
        Appearance wyglad_pret = new Appearance();
        Appearance wyglad_podstawka = new Appearance();
        Appearance wyglad_pudelko = new Appearance();
        Appearance wyglad_niebo = new Appearance();



        // Material podstawki, podstawka, wygląd
        Material metalowy = new Material();
        metalowy.setEmissiveColor( 0.2f, 0.2f, 0.2f );
        metalowy.setDiffuseColor( 0.5f, 0.5f, 0.5f);
        metalowy.setSpecularColor( 0.2f, 0.2f, 0.2f );
        metalowy.setShininess(50f);

        // Materiał pudełka, pudełko, wygląd
        Material pudelkowy = new Material();
        pudelkowy.setEmissiveColor( 0.0f, 0.5f, 0.1f );
        pudelkowy.setDiffuseColor( 0.9f, 0.3f, 0.3f);
        pudelkowy.setSpecularColor( 0.2f, 0.1f, 0.1f );
        pudelkowy.setShininess(50f);

        // Zastosowanie tekstur do elementow sceny
        wyglad_ramie.setMaterial(metalowy);
        wyglad_podstawka.setMaterial(metalowy);
        wyglad_pudelko.setMaterial(pudelkowy);
        

        //==============================
        //      Oświetlenie sceny
        //==============================
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),100);
        Color3f light1Color = new Color3f(1.0f,0.0f,0.2f); //kolor 
        Vector3f light1Direction = new Vector3f(4.0f,-7.0f,-12.0f); //kierunek
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        wezel_scena.addChild(light1); //dodanie do sceny

        //dodanie światła bezkierunkowego
        Color3f ambientColor = new Color3f(0.3f,0.3f,0.0f);
        AmbientLight ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        wezel_scena.addChild(ambientLightNode);

        //określanie transformat

        //objTrans = new TransformGroup();
        //objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        pozycja = new TransformGroup();
        wezel_scena.addChild(pozycja);

        rotacjaRamienia = new TransformGroup();
        pozycjaRamienia = new TransformGroup();
        Transform3D przesuniecie_pozycja_1 = new Transform3D();
        przesuniecie_pozycja_1.set(new Vector3f(0.0f,0.6f,0.0f)); //położenie ramienia
        pozycjaRamienia.setTransform(przesuniecie_pozycja_1);
            
        rotacjaRamienia.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE);//dodane
        rotacjaRamienia.setCapability( TransformGroup.ALLOW_TRANSFORM_READ);//dodane
        pozycjaRamienia.addChild(rotacjaRamienia);
        pozycja.addChild(pozycjaRamienia);

        rotacjaPrzedramienia = new TransformGroup();
        pozycjaPrzedramienia = new TransformGroup();
        Transform3D przesuniecie_pozycja_2 = new Transform3D();
        przesuniecie_pozycja_2.set(new Vector3f(0.0f,0.036f,0.44f)); //położenie przedramienia
        pozycjaPrzedramienia.setTransform(przesuniecie_pozycja_2);
    
        rotacjaPrzedramienia.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotacjaPrzedramienia.setCapability( TransformGroup.ALLOW_TRANSFORM_READ);
        pozycjaPrzedramienia.addChild(rotacjaPrzedramienia);
        rotacjaRamienia.addChild(pozycjaPrzedramienia);

        pozycjaWału = new TransformGroup();
        przesuniecieWału_GD = new TransformGroup();
        Transform3D przesuniecie_pozycja_3 = new Transform3D();
        przesuniecie_pozycja_3.set(new Vector3f(0.0f,0.0f,0.44f));
        przesuniecieWału_GD.setTransform(przesuniecie_pozycja_3);
            
        pozycjaWału.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE);
        pozycjaWału.setCapability( TransformGroup.ALLOW_TRANSFORM_READ);
        przesuniecieWału_GD.addChild(pozycjaWału);
        rotacjaPrzedramienia.addChild(przesuniecieWału_GD);

        //pudełko
        pudelko = new TransformGroup();
         pudelko.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        przesuniecie_pudla.set(new Vector3f(0.0f,0.08f,0.5f)); //(x,ile nad ziemią,y)
        pudelko.setTransform(przesuniecie_pudla);

        pudlo = new Box(0.08f,0.08f,0.08f,Box.GENERATE_NORMALS|Box.GENERATE_TEXTURE_COORDS,wyglad_pudelko);//wymiary pudla
        pudelko.addChild(pudlo);
        // RYZYKUJE ZAMIANE NA BRANCH
        branch_pudlo = new BranchGroup();
        branch_pudlo.setCapability(BranchGroup.ALLOW_DETACH);
        
        branch_pudlo.addChild(pudelko);
        wezel_scena.addChild(branch_pudlo);

        //podstawa
        TransformGroup podstawa = new TransformGroup();
        Transform3D przesuniecie_sciany = new Transform3D();
        przesuniecie_sciany.set(new Vector3f(0.0f,0.0f,0.0f));
        podstawa.setTransform(przesuniecie_sciany);

        Box sciana = new Box(0.12f,0.022f,0.12f,Box.GENERATE_NORMALS|Box.GENERATE_TEXTURE_COORDS,wyglad_podstawka);//wymiary podstawki
        podstawa.addChild(sciana);
        pozycja.addChild(podstawa);

        //cylinder (trzon)
        TransformGroup wieza_p = new TransformGroup();
        Transform3D przesuniecie_wiezy = new Transform3D();
        przesuniecie_wiezy.set(new Vector3f(0.0f,0.3f,0.0f));
        wieza_p.setTransform(przesuniecie_wiezy);

        Cylinder walec = new Cylinder(0.055f,0.6f); //trzon

        wieza_p.addChild(walec);
        pozycja.addChild(wieza_p);

        //ramię_1
        TransformGroup ramie_1 = new TransformGroup();
        Transform3D przesuniecie_ramie_1 = new Transform3D();
        przesuniecie_ramie_1.set(new Vector3f(0.0f,0.0f,0.22f));
        ramie_1.setTransform(przesuniecie_ramie_1);
        Box ramie1 = new Box(0.06f,0.018f,0.22f,wyglad_ramie);  //1-szerokość

        TransformGroup nakladka_11 = new TransformGroup();
        Transform3D przesuniecie_nakladka_11 = new Transform3D();
        przesuniecie_nakladka_11.set(new Vector3f(0.0f,0.00f,-0.22f));
        nakladka_11.setTransform(przesuniecie_nakladka_11);
        Cylinder nakladka11 = new Cylinder(0.0601f,0.036f,wyglad_ramie); // tuż przy trzonie
        nakladka_11.addChild(nakladka11);
        ramie_1.addChild(nakladka_11);

        TransformGroup nakladka_12 = new TransformGroup();
        Transform3D przesuniecie_nakladka_12 = new Transform3D();

        przesuniecie_nakladka_12.set(new Vector3f(0.0f,0.0f,0.22f));
        nakladka_12.setTransform(przesuniecie_nakladka_12);

        Cylinder nakladka12 = new Cylinder(0.06f,0.036f,wyglad_ramie);
        nakladka_12.addChild(nakladka12);
        ramie_1.addChild(nakladka_12);

        ramie_1.addChild(ramie1);
        rotacjaRamienia.addChild(ramie_1);

        //ramię 2
        TransformGroup ramie_2 = new TransformGroup();
        Transform3D przesuniecie_ramie_2 = new Transform3D();
        przesuniecie_ramie_2.set(new Vector3f(0.0f,0.0f,0.22f));
        ramie_2.setTransform(przesuniecie_ramie_2);
        Box ramie2 = new Box(0.06f,0.018f,0.22f,wyglad_ramie);

        TransformGroup nakladka_21 = new TransformGroup();
        Transform3D przesuniecie_nakladka_21 = new Transform3D();
        przesuniecie_nakladka_21.set(new Vector3f(0.0f,0.00f,-0.22f));
        nakladka_21.setTransform(przesuniecie_nakladka_21);
        Cylinder nakladka21 = new Cylinder(0.0601f,0.036f,wyglad_ramie);
        nakladka_21.addChild(nakladka21);
        ramie_2.addChild(nakladka_21);

        TransformGroup nakladka_22 = new TransformGroup();
        Transform3D przesuniecie_nakladka_22 = new Transform3D();

        przesuniecie_nakladka_22.set(new Vector3f(0.0f,0.0f,0.22f));
        nakladka_22.setTransform(przesuniecie_nakladka_22);

        Cylinder nakladka22 = new Cylinder(0.06f,0.036f,wyglad_ramie);
        nakladka_22.addChild(nakladka22);
        ramie_2.addChild(nakladka_22);
        ramie_2.addChild(ramie2);
        rotacjaPrzedramienia.addChild(ramie_2);

        // wał (pionowy)
        //ni chuj nie mam pojęcia który z tych hujów zadziałał
        pret_ = new TransformGroup();
        pret_.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
        pret_.setCapability(TransformGroup.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
        pret_.setCapability(TransformGroup.ALLOW_BOUNDS_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        pret_.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        pret_.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_COLLIDABLE_READ);
        pret_.setCapability(TransformGroup.ALLOW_COLLIDABLE_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_COLLISION_BOUNDS_READ);
        pret_.setCapability(TransformGroup.ALLOW_COLLISION_BOUNDS_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_LOCALE_READ);
        pret_.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        pret_.setCapability(TransformGroup.ALLOW_PARENT_READ);
        pret_.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
        pret_.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
        pret_.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        pret_.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        pret_.setCapability(TransformGroup.ENABLE_COLLISION_REPORTING);
        pret_.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        przesuniecie_pret_.set(new Vector3f(0.0f,0.0f,0.0f));
        pret_.setTransform(przesuniecie_pret_);
        pret = new Cylinder(0.025f,0.65f); //wymiary pręta
        pret_.addChild(pret);
        pozycjaWału.addChild(pret_);

        //podłoże - załadowanie tekstury
        TextureLoader loader = new TextureLoader("obrazki/murek.png",null);
        ImageComponent2D image = loader.getImage();
        Texture2D podloze = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());

        podloze.setImage(0, image);
        podloze.setBoundaryModeS(Texture.WRAP);
        podloze.setBoundaryModeT(Texture.WRAP);

        //podłoże (kafelki)
        wyglad_ziemia.setTexture(podloze);
        Point3f[]  coords = new Point3f[4];
        for(int i = 0; i< 4; i++)     coords[i] = new Point3f();
        Point2f[]  tex_coords = new Point2f[4];
        for(int i = 0; i< 4; i++)     tex_coords[i] = new Point2f();

        coords[0].y = 0.0f;
        coords[1].y = 0.0f;
        coords[2].y = 0.0f;
        coords[3].y = 0.0f;
        coords[0].x = 3.5f;
        coords[1].x = 3.5f;
        coords[2].x = -3.5f;
        coords[3].x = -3.5f;
        coords[0].z = 3.5f;
        coords[1].z = -3.5f;
        coords[2].z = -3.5f;
        coords[3].z = 3.5f;

        tex_coords[0].x = 0.0f;
        tex_coords[0].y = 0.0f;
        tex_coords[1].x = 10.0f;
        tex_coords[1].y = 0.0f;
        tex_coords[2].x = 0.0f;
        tex_coords[2].y = 10.0f;
        tex_coords[3].x = 10.0f;
        tex_coords[3].y = 10.0f;

        QuadArray qa_ziemia = new QuadArray(4, GeometryArray.COORDINATES|GeometryArray.TEXTURE_COORDINATE_2);
        qa_ziemia.setCoordinates(0,coords);
        qa_ziemia.setTextureCoordinates(0, tex_coords);
        Shape3D ziemia = new Shape3D(qa_ziemia);
        ziemia.setAppearance(wyglad_ziemia);
        wezel_scena.addChild(ziemia);

        // OTOCZENIE ROBOTA

        //niebo - załadowanie tekstury
        TextureLoader loader2 = new TextureLoader("obrazki/Clouds2.png",null);
        ImageComponent2D image2 = loader2.getImage();
        Texture2D chmury = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image2.getWidth(), image2.getHeight());

        chmury.setImage(0, image2);
        chmury.setBoundaryModeS(Texture.WRAP);
        chmury.setBoundaryModeT(Texture.WRAP);
        wyglad_niebo.setTexture(chmury);
        
        //Niebo jako wnętrze ogromnej sfery
        Sphere sky = new Sphere(10f, Sphere.GENERATE_NORMALS_INWARD| Sphere.GENERATE_TEXTURE_COORDS, wyglad_niebo); //tworzenie nieba z tekstura do srodka
        wezel_scena.addChild(sky);
        
        return wezel_scena;
    }
    //obsługiwanie ramienia

    public static void main(String[] args) {
        robot bb = new robot();
        bb.addKeyListener(bb);
        MainFrame mf = new MainFrame(bb,900,500); // wielkość okna programu
    }

}