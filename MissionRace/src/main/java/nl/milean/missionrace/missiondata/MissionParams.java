package nl.milean.missionrace.missiondata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michiel on 13-3-2016.
 */
public class MissionParams {
    private static MissionParams ourInstance = new MissionParams();

    public static MissionParams getInstance() {
        return ourInstance;
    }

    private List<Mission> missions;

    public Mission getMission(int number) {
        if (number >= 0 && number < missions.size()) {
            return missions.get(number);
        } else {
            return null;
        }
    }

    public int size() {
        return missions.size();
    }

    private MissionParams() {

        missions = new ArrayList<Mission>();

        //list of missions
        {
            Mission m = new Mission(MissionTypes.MessageOnly);
            m.addData("title", "Demo Missies");
            m.addData("message", "Hierna volgen een aantal voorbeeld missies. Er is " +
                    "precies 1 voorbeeld missie pet missietype.");
            m.addData("info", "Achter de info knop kan extra uitgebreide informatie gezet " +
                    "worden. Dit kan handig zijn om hints te geven, om informatie die al " +
                    "bekend hoort te zijn nogmaals te herhalen, enzovoort.\n\n" +
                    "Al deze voorbeeld missies zijn uitgevoerd op het evenement van 18 juni 2016," +
                    "wat georganiseerd werd door Rinie, Joeri, Sytske en Michiel.");
            missions.add(m);
        }
        {
            // Voorbeeld missie Chest
            float chestThreshold = 15;
            ChestLocationInfo[] locs = new ChestLocationInfo[]{
                    new ChestLocationInfo("Rood",                                                       //name
                            51.555572, 5.085326, chestThreshold,                                         //lat, long, tres
                            ChestLocationInfo.FREE, ChestLocationInfo.RED, ChestLocationInfo.YELLOW,    //locked/free, colour, contents
                            ChestLocationInfo.LOCK, ChestLocationInfo.RED, ChestLocationInfo.BLUE,      //locked/free, key, contents
                            ChestLocationInfo.LOCK, ChestLocationInfo.RED, ChestLocationInfo.A),        //locked/free, key, contents
                    new ChestLocationInfo("Geel",
                            51.555253, 5.086737, chestThreshold,
                            ChestLocationInfo.FREE, ChestLocationInfo.YELLOW, ChestLocationInfo.BLUE,
                            ChestLocationInfo.LOCK, ChestLocationInfo.YELLOW, ChestLocationInfo.RED,
                            ChestLocationInfo.LOCK, ChestLocationInfo.YELLOW, ChestLocationInfo.B),
                    new ChestLocationInfo("Blauw",
                            51.554401, 5.086148, chestThreshold,
                            ChestLocationInfo.FREE, ChestLocationInfo.BLUE, ChestLocationInfo.RED,
                            ChestLocationInfo.LOCK, ChestLocationInfo.BLUE, ChestLocationInfo.C,
                            ChestLocationInfo.LOCK, ChestLocationInfo.BLUE, ChestLocationInfo.YELLOW),
                    new ChestLocationInfo("Groen",
                            51.554758, 5.084511, chestThreshold,
                            ChestLocationInfo.LOCK, ChestLocationInfo.PARTS, ChestLocationInfo.EGG)
            };

            Mission m = new Mission(MissionTypes.Chests);
            m.addData("chests", locs);
            m.addData("title", "Laat je niet kisten");
            m.addData("info", "Op de kaart staan vier locaties aangegeven waar virtuele kisten staan. Loop naar deze locaties om de kisten te openen. In je inventaris bovenaan het scherm kun je zien wat je al hebt gevonden. In één van de kisten vind je een klepel. \n" +
                    "Tip: houdt de app aan tijdens deze missie.\n" +
                    "Zodra jullie de klepel hebben gevonden, gaan jullie automatisch door naar de volgende missie.");
            missions.add(m);
        }
        {
            // Voorbeeld missie DistancePhotoHidden
            Mission m = new Mission(MissionTypes.DistancePhotoHidden);
            m.addData("title","Op de voet volgen");
            m.addData("targetLat",new Double(51.84295));
            m.addData("targetLon",new Double(5.853406));
            m.addData("threshold", new Float(15));
            m.addData("text", "Vind de locatie vanwaar deze foto is gemaakt.");
            m.addData("photo", "wally");
            m.addData("info", "Loop naar de exacte locatie waar de fotograaf stond toen deze foto werd gemaakt. Houd je scherm aan voor betere gps-controle zodat de locatie gedetecteerd kan worden.\n");
            missions.add(m);
        }
        {
            // Voorbeeld missie DistanceQuestion
            Mission m = new Mission(MissionTypes.DistanceQuestion);
            m.addData("title", "Achter de feiten aan");
            m.addData("targetLat", new Double(51.682665));
            m.addData("targetLon", new Double(5.296862));
            m.addData("question", "Wat is het 40e woord in het gedicht?");
            m.addData("answers", new String[]{"oog"});
            m.addData("info", "Vind de locatie voor deze missie door stapje voor stapje dichterbij " +
                    "te komen. Het aantal meters dat je nog van de locatie vandaan bent, zal langzaamaan " +
                    "minder moeten worden.\nNa het invullen van het 40e woord van het gedicht ga je verder " +
                    "naar je volgende missie.\nTip: Hou het scherm van je smartphone aan om constante " +
                    "updates te krijgen van het GPS signaal en dus van je locatie!\n(deze tekst staat " +
                    "tijdens de missie ook achter de info-knop)");
            missions.add(m);
        }
        {
            // Voorbeeld missie DistanceThreshold
            Mission m = new Mission(MissionTypes.DistanceThreshold);
            m.addData("title", "Grote stappen snel thuis");
            m.addData("targetLat", new Double(51.764951));
            m.addData("targetLon", new Double(5.529294));
            m.addData("threshold", new Float(15));
            m.addData("info", "Vind je volgende locatie door stapje voor stapje dichterbij de " +
                    "locatie te komen. Het aantal meters dat je nog van de locatie vandaan bent, " +
                    "zal langzaamaan minder moeten worden.\n\nTip: Hou het scherm van je " +
                    "smartphone aan om constante updates te krijgen van het GPS signaal en " +
                    "dus van je locatie!");
            missions.add(m);
        }
        {
            // Voorbeeld missie DistanceThresholdHidden
            Mission m = new Mission(MissionTypes.DistanceThresholdHidden);
            m.addData("title", "Simpele ziel");
            m.addData("targetLat", new Double(51.843254));
            m.addData("targetLon", new Double(5.853074));
            // Wat ruim gekozen. In het station is GPS wat slechter, en als je pas weer oplet op het stationsplein moet je hem ook nog approved krijgen.
            m.addData("threshold", new Float(100));
            m.addData("message", "Reis naar Nijmegen.");
            m.addData("info", "Neem de trein naar station Nijmegen, daar krijg je je nieuwe missie.");
            missions.add(m);
        }
        {
            // Voorbeeld missie MessageOnly
            Mission m = new Mission(MissionTypes.MessageOnly);
            m.addData("title", "Achter de feiten aan");
            m.addData("message", "Vind de locatie voor deze missie door stapje voor stapje dichterbij " +
                    "te komen. Het aantal meters dat je nog van de locatie vandaan bent, zal langzaamaan " +
                    "minder moeten worden.\nTip: Hou het scherm van je smartphone aan om constante " +
                    "updates te krijgen van het GPS signaal en dus van je locatie!\n(deze tekst staat " +
                    "tijdens de missie ook achter de info-knop)");
            m.addData("info", "Vind de locatie voor deze missie door stapje voor stapje dichterbij " +
                    "te komen. Het aantal meters dat je nog van de locatie vandaan bent, zal langzaamaan " +
                    "minder moeten worden.\nTip: Hou het scherm van je smartphone aan om constante " +
                    "updates te krijgen van het GPS signaal en dus van je locatie!\n(deze tekst staat " +
                    "tijdens de missie ook achter de info-knop)");
            missions.add(m);
        }
        {
            // Voorbeeld missie Photo
            Mission m = new Mission(MissionTypes.Photo);
            m.addData("title", "Als twee druppels water");
            m.addData("photo", "testkaart1");
            m.addData("text","Vind de volgende ansichtkaart op de Lange Hezelstraat, en stuur " +
                    "deze op naar een familielid.\nStuur een foto naar de spelleiders waar een " +
                    "teamlid en de ansichtkaart duidelijk op te herkennen zijn");
            m.addData("info","Koop de juiste ansichtkaart op de Lange Hezelstraat in Nijmegen, " +
                    "koop een postzegel, schrijf het adres van een familielid op de kaart.\n\n" +
                    "Maak een duidelijke foto van de voorkant van de kaart, de achterkant van de " +
                    "kaart en dat jullie bij een brievenbus zijn!\nStuur je foto's als bewijs " +
                    "via whatsapp naar de spelleiders. Als het bewijs wordt goedgekeurd, krijg " +
                    "je automatisch toegang naar de volgende missie. Graag even geduld.");
            missions.add(m);
        }
        {
            // Voorbeeld missie Question
            Mission m = new Mission(MissionTypes.Question);
            m.addData("title", "Test");
            m.addData("question", "Wie is je favoriete spelleider van de Amazing Race 2016?");
            m.addData("answers", new String[]{"joeri", "michiel", "rinie", "sytske"});
            m.addData("info", "Vul de voornaam in van je favoriete spelleider van de Amazing Race 2016. Je kunt dus kiezen uit Michiel, Joeri, Sytske of Rinie. Bij het correcte antwoord ga je verder naar de volgende missie!");
            missions.add(m);
        }
        {
            // Voorbeeld missie Reveal
            List<String> questions = new ArrayList<String>();
            List<String[]> answers = new ArrayList<String[]>();

            questions.add("Wat gaf God aan Sint Ontcommer?");
            answers.add(new String[]{"baard", "een baard", "zwarte baard", "een zwarte baard"});

            questions.add("Welk jaartal staat er boven de poort naar de Zuiderkapel?");
            answers.add(new String[]{"1953"});

            questions.add("In welk jaar is Franciscus Burmannus “vertrocken”?");
            answers.add(new String[]{"1743"});

            questions.add("Wat is de voornaam van de dominicaan die de kerk inwijdde?");
            answers.add(new String[]{"Albertus"});

            questions.add("Hoeveel vogels staan er afgebeeld op het graf van Adriaen van Arnhem (nabij de Zuiderkapel)?");
            answers.add(new String[]{"6", "zes"});

            questions.add("Hoeveel pijpen heeft het grootste orgel van deze kerk?");
            answers.add(new String[]{"3600"});

            questions.add("Hoe heette het altaar in de sacristie in 1565?");
            answers.add(new String[]{"gertrudisaltaar", "gertrudis-altaar", "gertrudis", "gertrudis altaar"});

            questions.add("In welk jaar is Rumoldus Rombouts ontslagen?");
            answers.add(new String[]{"1669"});

            questions.add("Wie lag er als eerste begraven in het graf in de poort tussen nummer 18 en 25 op de plattegrond van de informatiefolder?");
            answers.add(new String[]{"Anna Snoecx"});

            questions.add("Wat is de naam van Sint Steven in het Spaans?");
            answers.add(new String[]{"San Esteban", "San Estaban"});

            questions.add("hoe noem je iemand die jaarlijks 365 euro doneert aan de stevenskerk?");
            answers.add(new String[]{"een stevenier","stevenier"});

            questions.add("Welke Paus verhief de Stevenskerk tot kapittelkerk?");
            answers.add(new String[]{"Paus Sixtus IV", "Sixtus IV", "Paus Sixtus 4", "Sixtus 4", "Paus Sixtus de 4e", "Sixtus de 4e", "Paus Sixtus de vierde", "Sixtus de vierde"});

            Mission m = new Mission(MissionTypes.Reveal);
            m.addData("message", "Race naar de volgende locatie. Druk op info voor meer speluitleg.");
            m.addData("title", "Heilig boontje");
            m.addData("questions", questions);
            m.addData("answers", answers);
            m.addData("tilesW", 6);
            m.addData("tilesH", 6);
            m.addData("revealOrder", new Integer[]{3,22,31,6,10,29,9,12,32,1,17,25,4,24,34,0,8,33,2,16,30,5,19,35,15,18,27,11,13,20,14,21,23,7,26,28});
            m.addData("photo", "cafemiddagpauze");
            m.addData("info", "Verzamel meer stukken van de foto door vragen correct te beantwoorden." +
                    " Alle vragen zijn te beantwoorden in de St. Stevenskerk te Nijmegen.\nBij een " +
                    "goed antwoord verschijnen nieuwe delen van de foto. Na het beantwoorden van een " +
                    "vraag (goed of fout) krijg je vanzelf de volgende vraag. Als je door alle vragen " +
                    "heen bent, kom je vanzelf weer bij de eerste vraag die je nog niet correct " +
                    "beantwoord hebt.\n\nAls je denkt dat je weet welke locatie op de foto staat, " +
                    "race daarheen voor het tweede Breakpoint. Neem het kaartje met de tijd waarop " +
                    "je verder mag racen. Het bovenste kaartje is altijd de snelste om te pakken.");
            missions.add(m);
        }
        {
            // Example missie Text
            Mission m = new Mission(MissionTypes.Text);
            m.addData("title", "Op rolletjes");
            m.addData("message", "Maak een foto waarop het volgende zichtbaar is:\n- Minstens één deelnemer van jullie team op een roltrap.");
            m.addData("info", "Stuur je foto via whatsapp naar de spelleiders. Als de foto wordt goedgekeurd, krijg je automatisch toegang naar de volgende missie. Graag even geduld.");
            missions.add(m);
        }
    }

}
