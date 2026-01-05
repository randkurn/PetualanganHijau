package model;

import java.util.Random;

public class ObjectManager {
    static Random rand = new Random();

    public static GameObject createObject(int index) {
        switch (index) {
            case 1:
                int r = rand.nextInt(12);
                return switch (r) {
                    case 0 ->
                        GameObject.createTrash("Botol Minum", "/objects/trash/botol cola.png", 15, TrashType.ANORGANIC);
                    case 1 -> GameObject.createTrash("Sisa Pisang", "/objects/trash/pisang.png", 5, TrashType.ORGANIC);
                    case 2 ->
                        GameObject.createTrash("Baterai Bekas", "/objects/trash/baterai.png", 10, TrashType.TOXIC);
                    case 3 -> GameObject.createTrash("Bohlam Putus", "/objects/trash/bohlam.png", 10, TrashType.TOXIC);
                    case 4 ->
                        GameObject.createTrash("Charger Rusak", "/objects/trash/charger.png", 12, TrashType.TOXIC);
                    case 5 -> GameObject.createTrash("Kaleng Makanan", "/objects/trash/kaleng makanan.png", 8,
                            TrashType.ANORGANIC);
                    case 6 -> GameObject.createTrash("Masker Bekas", "/objects/trash/masker.png", 5, TrashType.TOXIC);
                    case 7 -> GameObject.createTrash("Sisa Apel", "/objects/trash/sisa apel.png", 5, TrashType.ORGANIC);
                    case 8 -> GameObject.createTrash("Pop Es", "/objects/trash/pop es.png", 10, TrashType.ANORGANIC);
                    case 9 -> GameObject.createTrash("Botol Tertanam", "/objects/trash/botol aqua tertanam.png", 15,
                            TrashType.ANORGANIC);
                    case 10 -> GameObject.createTrash("Susu Kotak", "/objects/trash/susu.png", 10, TrashType.ANORGANIC);
                    default -> GameObject.createTrash("Ceri Busuk", "/objects/trash/ceri.png", 5, TrashType.ORGANIC);
                };
            case 2:
                return GameObject.createSimple(GameObject.Type.KEY, "Kunci", "/objects/key.png", true);
            case 3:
                return GameObject.createSimple(GameObject.Type.GATE, "Pintu Kayu", "/objects/closedgate.png", true);
            case 4:
                return GameObject.createSimple(GameObject.Type.PORTAL, "Portal", "/objects/openedgate.png", false);
            case 7:
                return GameObject.createSimple(GameObject.Type.BED, "Tempat Tidur", "/objects/bed.png", true);
            case 8:
                return GameObject.createBin("Tempat Sampah Hijau", "/objects/trashbin/hijau.png",
                        "Untuk sampah organik.", TrashType.ORGANIC);
            case 9:
                return GameObject.createBin("Tempat Sampah Kuning", "/objects/trashbin/kuning.png",
                        "Untuk sampah anorganik.", TrashType.ANORGANIC);
            case 10:
                return GameObject.createBin("Tempat Sampah Merah", "/objects/trashbin/merah.png",
                        "Untuk sampah berbahaya.", TrashType.TOXIC);
            case 11:
                return new OBJ_TreeSeed();
            default:
                return null;
        }
    }
}
