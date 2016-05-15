import java.io.*;
import java.util.*;
import onLineBanking.*;
import org.omg.CORBA.*;
import java.util.Date.*;

public class OnLineBankingServant implements OnLineBankingOperations{
	public static Hashtable hashtable;
	public static ORB orb;
	public static String UniqueID;
	public static String ESBID;
	public static Integer ID_Counter;


public OnLineBankingServant (org.omg.CORBA.ORB orb){
	hashtable = new Hashtable();
	this.orb = orb;

	//Setup the ESB a/c
	//String timestamp=new Date().toString();
	ESBID = "888";
	onLineDetails ESBonLineDetails = new onLineDetails();
	ESBonLineDetails.CustomerName = "ESB";
	ESBonLineDetails.CustomerAddress = "Dublin";
	ESBonLineDetails.Balance = 0;
	ESBonLineDetails.BankACNumber=100;
	ESBonLineDetails.UniqueID = ESBID;

	ID_Counter = 1111;

	hashtable.put(ESBonLineDetails.UniqueID, ESBonLineDetails);

}
     //Server registers Client's details by Servant
public void registerOnLineBanking (org.omg.CORBA.Any anyOnLineDetails, StringHolder password){
	String timestamp=new Date().toString();
//	UniqueID = timestamp;
	UniqueID = Integer.toString(++ID_Counter);
	onLineDetails myonLineDetails = onLineDetailsHelper.extract(anyOnLineDetails);
	myonLineDetails.UniqueID = UniqueID;
	hashtable.put(UniqueID, myonLineDetails);
	password.value = UniqueID;

	}

// This will just implement payment to the ESB account //
public void performOnLineTransaction (String UniqueID, int amount, AnyHolder anyOnLineDetails){
	try{
		onLineDetails myonLineDetails = (onLineDetails)hashtable.get(UniqueID);
		onLineDetails ESBonLineBanking = (onLineDetails) hashtable.get(ESBID);
		myonLineDetails.Balance -= amount;
		ESBonLineBanking.Balance += amount;
		System.out.println("ESB Balance : "+ESBonLineBanking.Balance + ". Received " + amount + " from: "+myonLineDetails.CustomerName);

		Any anyOnLine = orb.create_any();
		onLineDetailsHelper.insert(anyOnLine, myonLineDetails);
		anyOnLineDetails.value = anyOnLine;

		hashtable.put(UniqueID, myonLineDetails);
		hashtable.put(ESBID, ESBonLineBanking);
		}catch (Exception e) {System.err.println(e);
		e.printStackTrace(System.out);
		}
	}
}