package josh.passportSetting;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;

import org.jmrtd.BACKey;
import org.jmrtd.PassportService;

import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.protocol.BACResult;

import javax.smartcardio.*;
import java.io.IOException;
import java.io.InputStream;


public class Main {
    static final int MAX_TRANSCEIVE_LENGTH = PassportService.NORMAL_MAX_TRANCEIVE_LENGTH;
    static final int MAX_BLOCK_SIZE = PassportService.DEFAULT_MAX_BLOCKSIZE;
    static final boolean IS_SFI_ENABLED = false;
    static final boolean SHOULD_CHECK_MAC = false;

    public static void main(String[] args)throws CardServiceException, CardException, IOException {
	// write your code here
        BACKey bacKey = new BACKey("A12345678","990922","221231");

        //get terminal (card reader)
        CardTerminal terminal = TerminalFactory.getDefault().terminals().list().get(0);
        System.out.println(terminal.getName());

        CardService cs = CardService.getInstance(terminal);
        PassportService ps = new PassportService(cs,MAX_TRANSCEIVE_LENGTH,
                MAX_BLOCK_SIZE,IS_SFI_ENABLED,SHOULD_CHECK_MAC);
        ps.open();

        //select passport applet
//        System.out.println(new String(ps.transmit(new CommandAPDU(0x00,0xa4,0x04,0x00,new byte[]{(byte)0xa0,0x00,0x00,0x02,0x47,0x10,0x01},0x00)).getData()));
        ps.sendSelectApplet(false);

        //doBAC (basic access control)
        System.out.println(bacKey);
        BACResult bacResult = ps.doBAC(bacKey);
        System.out.println(bacResult);

        //testing
//        CommandAPDU commandAPDU = new CommandAPDU(0x00,(byte)0xff,0x00,0x00,new byte[]{},0x00);
//        ResponseAPDU responseAPDU = ps.transmit(commandAPDU);
//        System.out.println(new String(responseAPDU.getData()));




        //read file
        InputStream is = ps.getInputStream(PassportService.EF_DG1, MAX_BLOCK_SIZE);
//        byte[] buffer = new byte[256];
//        int result = is.read(buffer);
//        System.out.println(new String(buffer));
        DG1File dg1File = new DG1File(is);
        System.out.println(dg1File.getMRZInfo());
        ps.close();
    }
}
