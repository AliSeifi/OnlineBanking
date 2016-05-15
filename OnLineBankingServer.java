import onLineBanking.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;

public class OnLineBankingServer{
public static void main(String args[]){
	try{
		org.omg.CORBA.ORB orb = ORB.init(args, null);

		org.omg.CORBA.Object nameObj=orb.resolve_initial_references("NameService");

		NamingContext rootCtx=NamingContextHelper.narrow(nameObj);
		NameComponent[] name = new NameComponent[1];
		name[0] = new NameComponent("Banking", "Ctx");
		NamingContext BankingCtx = rootCtx.bind_new_context(name);

		name[0] = new NameComponent("BankingOnLine", "Object");

		OnLineBanking servant = new OnLineBanking_Tie(new OnLineBankingServant(orb));

		BankingCtx.rebind(name, servant);

		System.out.println("Online banking server started ...");
		orb.run();

		}catch (Exception e){
			System.err.println("Error E: "+e);
			e.printStackTrace(System.out);
		}
	}
}