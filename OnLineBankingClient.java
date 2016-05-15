import onLineBanking.*;
import java.io.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;

public class OnLineBankingClient{

	public static void main(String args[]){
		try{
			org.omg.CORBA.ORB orb = ORB.init(args, null);

			//Retrieve BAnking Object reference from the NameService
			org.omg.CORBA.Object nameObj=orb.resolve_initial_references("NameService");
			NamingContext rootCtx=NamingContextHelper.narrow(nameObj);
			NameComponent[] name = new NameComponent[2];
			name[0] = new NameComponent("Banking", "Ctx");
			name[1] = new NameComponent("BankingOnLine", "Object");
			org.omg.CORBA.Object obj = rootCtx.resolve(name);
			OnLineBanking servantref=OnLineBankingHelper.narrow(rootCtx.resolve(name));
           //what is the below section, client's details?
			onLineDetails myOnLineDetails = new onLineDetails();
			Any anyOnLineDetails;
			String tempName;
			int menuChoice;
			StringHolder password = new StringHolder();
			Any anyMyOnLineDetails = orb.create_any();

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			do{
				System.out.println("Please enter desired menu option");
				System.out.println(" 1 - Register for On Line Banking");
				System.out.println(" 2 - Pay your ESB bill online");
				System.out.println(" 3 - Quit");
				menuChoice = Integer.parseInt(reader.readLine());

				switch (menuChoice){
					case 1: {
						System.out.println("Please enter a name : ");
						myOnLineDetails.CustomerName=reader.readLine();
						System.out.println("Please enter an address : ");
						myOnLineDetails.CustomerAddress = reader.readLine();
						System.out.println("Please enter a Bank Account Number : ");
						myOnLineDetails.BankACNumber = Integer.parseInt(reader.readLine());
						System.out.println("Please enter a Balance : ");
						myOnLineDetails.Balance = Integer.parseInt(reader.readLine());
						myOnLineDetails.UniqueID = "";

						onLineDetailsHelper.insert(anyMyOnLineDetails, myOnLineDetails);

						//create Request object. Note operation name is sayHello - client sends request to servant and server
						Request request = servantref._request("registerOnLineBanking");
						Any arg = request.add_in_arg(); 		//add an argument to the Request object
						arg.insert_any(anyMyOnLineDetails);     //Inserting a value into the any
						Any arg2 = request.add_out_arg();

						request.set_return_type(orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string));
						request.invoke();

						String passwordValue = request.return_value().extract_string();
						System.out.println("Your secret password is : "+passwordValue);

						continue;
					}// End case 1
					case 2: {
						System.out.println("Please enter your secret number : ");
						tempName = reader.readLine();
						System.out.println("Please enter the amount you want to pay to ESB :");
						int amount = Integer.parseInt(reader.readLine());

						//create Request object. Note operation name is sayHello
						Request request = servantref._request("performOnLineTransaction");
						Any arg = request.add_in_arg(); 	//add an argument to the Request object
						arg.insert_string(tempName);      	//Inserting a value into the any
						Any arg2 = request.add_in_arg();
						arg2.insert_long(amount);
						Any arg3 = request.add_out_arg();

						request.set_return_type(orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any));
						request.invoke();

						anyOnLineDetails = request.return_value().extract_any();
						onLineDetails temponLineDetails = new onLineDetails();
						temponLineDetails = onLineDetailsHelper.extract(anyOnLineDetails);
						//Check TypeCode
						TypeCode tconLineDetails = anyOnLineDetails.type();
						if(onLineDetailsHelper.type().equal(tconLineDetails)){
							System.out.println("\nName 	: "+temponLineDetails.CustomerName);
							System.out.println("Address 	: "+temponLineDetails.CustomerAddress);
							System.out.println("Balance : "+temponLineDetails.Balance);
							System.out.println("--- End of Transaction ---\n");
						}
						else
							System.out.println("Wrong Type returned");
						continue;
					}//End case 2
			}// End switch
			}while (menuChoice!=3);
		}catch (Exception e) {
			System.err.println("Error E: "+e);
			e.printStackTrace(System.out);
		}
	}
}
