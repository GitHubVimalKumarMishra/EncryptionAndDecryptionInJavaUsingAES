import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class ZipUnzDel {
    static File zipDel(File selFol) throws Exception {
        File zipFil = new File(selFol.getAbsolutePath() + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFil); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipAdd("", selFol, zos);
            del(selFol);
        }
        return zipFil;
    }

    private static void zipAdd(String parFol, File selFol, ZipOutputStream zos) throws IOException {
        for (File folFil : selFol.listFiles()) {
            if (folFil.isDirectory()) {
                zipAdd(parFol + folFil.getName() + "/", folFil, zos);
            } else {
                zos.putNextEntry(new ZipEntry(parFol + folFil.getName()));
                try (FileInputStream fis = new FileInputStream(folFil)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        zos.write(buf, 0, len);
                    }
                }
            }
        }
    }

    private static void del(File selFol) {
        for (File folFil : selFol.listFiles()) {
            if (folFil.isDirectory()) {
                del(folFil);
            } else {
                folFil.delete();
            }
        }
        selFol.delete();
    }

    static void unzDel(File selFil) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(selFil))) {
            File unzFol = new File(selFil.getParentFile(), selFil.getName().replace(".zip", ""));
            unzFol.mkdir();
            ZipEntry ent = zis.getNextEntry();
            while (ent != null) {
                File fil = new File(unzFol, ent.getName());
                if (ent.isDirectory()) {
                    fil.mkdirs();
                } else {
                    fil.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(fil)) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = zis.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                        }
                    }
                }
                ent = zis.getNextEntry();
            }
            zis.close();
            if (unzFol.listFiles().length == 0) {
                unzFol.delete();
            } else {
                selFil.delete();
            }
        }
    }
}