import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


public class DavisBaseLite {
	
	static String prompt = "davisql> ";
	
	static String schemaname ="information_schema";


	public static void main(String[] args) {
		
		splashScreen();
		
		MakeInformationSchema.main(args);

		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand; 

		do {  
			System.out.print(prompt);
			userCommand = scanner.next().trim();

			String[] cmd = userCommand.split(" ");

			if(userCommand.equalsIgnoreCase("SHOW SCHEMAS"))
			{
				showShema();
			}
			else if(userCommand.equalsIgnoreCase("SHOW TABLES"))
			{
				showTables();
			}
			else if(cmd[0].equalsIgnoreCase("USE"))
			{
				useSchema(userCommand+";");
			}
			else if(cmd[0].equalsIgnoreCase("SELECT"))
			{
				
				if(userCommand.contains("where")|| userCommand.contains("WHERE"))
					displaysingle(userCommand);
				else 
					select(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("INSERT"))
			{
				insert(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("CREATE") && cmd[1].equalsIgnoreCase("TABLE"))
			{
				splitter(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("CREATE") && cmd[1].equalsIgnoreCase("SCHEMA"))
			{
				createSchema(userCommand+";");
			}
			else if(userCommand.equalsIgnoreCase("version"))
			{
				version();
			}
			else if(userCommand.equalsIgnoreCase("help"))
			{
				help();
			}
			else if(userCommand.equalsIgnoreCase("exit"))
			{

			}
		}while(!userCommand.equals("exit"));
		System.out.println("Exiting...");
	}
		
	

	private static void displaysingle(String userCommand) {
		String[] t = null ;
		if(userCommand.contains("WHERE"))
			 t = userCommand.split("WHERE");
		if(userCommand.contains("where"))
			 t = userCommand.split("where");
		

		String[] token = t[0].split(" ");
		String tablename = token[3];
		String col="";
		String val="";
		String cond = "";
		String c[] = t[1].split("[=\\>\\<\\ ]"); 
		if(t[1].contains("="))
		{
			col=c[1];
			val=c[c.length-1];
			cond= "=";
		}
		if(t[1].contains("<"))
		{
			col=c[1];
			val=c[c.length-1];
			cond= "<";
		}
		if(t[1].contains(">"))
		{
			col=c[1];
			val=c[c.length-1];
			cond= ">";
		}
		
		
		/***************************************************************************/
		ArrayList col_name = new ArrayList<String>();
		ArrayList type_name = new ArrayList<String>();
		try{
			
		RandomAccessFile f1 = new RandomAccessFile ("information_schema.columns.tbl","rw");
		int flag=0, flag1=0;
		int num;
		String type1="",ty="",y="",colname="";
		for(int j=0;j<f1.length(); j++)
		{	
			flag=0;
			flag1=0;
			ty="";
			 colname="";

			if(f1.getFilePointer()<f1.length()) {
				for(int  k=0; k<3 ;k++){
					String str="";
					byte l = f1.readByte();

					for(int i=0; i<l; i++)
						str += (char)f1.readByte();
					if(schemaname.equalsIgnoreCase(str)){
						flag=1;
					}
					if(tablename.equalsIgnoreCase(str) && flag==1)
					{
						flag1=1;
					}
					if(k==2 && flag1==1)
					{
						 colname = str;
					}

				}
				 f1.readInt();
				
				byte lo= f1.readByte();
				for(int i=0; i<lo; i++)
					ty += (char)f1.readByte();

				if(flag1==1)
				{
					type1= ty;
				}


				byte p= f1.readByte();
				for(int j1=0; j1<p; j1++)
					y += (char) f1.readByte();

				byte p1= f1.readByte();

				for(int j1=0; j1<p1; j1++)
					y += (char) f1.readByte();
				
			if(flag1==1){
			col_name.add(colname);
			type_name.add(type1);
			}
			}
		}
		}
		catch(Exception e)
		{
			
		}
		
		
	
		/**********************************************************************************/
		int flg=0;
		System.out.println("-------------------------------------------------------------------------------");
		for(int i=0; i<col_name.size();i++)
		{
			System.out.print(col_name.get(i)+"  |");
			if(((String) col_name.get(i)).equalsIgnoreCase(col))
				flg=i;
			
		}
		System.out.println("\n-------------------------------------------------------------------------------");
		
		String type="";
		String coltype=(String)type_name.get(flg);
		try {
			RandomAccessFile  f = new RandomAccessFile(schemaname+"."+tablename+".tbl","rw");
			
			ArrayList<ArrayList<String>> list1 = new ArrayList<ArrayList<String>>();
			
			for(int i=0;i<f.length();i++)
			{
				if(f.getFilePointer()<f.length())
				{
					ArrayList<String> list = new ArrayList<String>();
					for(int j=0;j<type_name.size();j++)
					{
						 type = (String) type_name.get(j);
						 
							if(type.equalsIgnoreCase("int"))
							{
								int k =f.readInt();
								list.add(Integer.toString(k));
							}
							else if(type.equalsIgnoreCase("long") || type.equalsIgnoreCase("long int"))
							{
								long k= f.readLong();
								list.add(Long.toString(k));
							}
							else if(type.equalsIgnoreCase("short") || type.equalsIgnoreCase("short int"))
							{
								short k = f.readShort();
								list.add(Short.toString(k));
							}
							else if(type.equalsIgnoreCase("float"))
							{
								float k= f.readFloat();
								list.add(Float.toString(k));
							}
							else if(type.equalsIgnoreCase("double"))
							{
								double k=f.readDouble();
								list.add(Double.toString(k));
							}
							else if(type.equalsIgnoreCase("byte"))
							{
								byte k =f.readByte();
								list.add(Byte.toString(k));
							}
							else 
							{	
								int length = f.readByte();
								String out="";
								for(int g=0; g< length ;g++)
								{
									out+= (char)f.readByte();
								}
								list.add(out);
							}
					}
					
					list1.add(list);
				}
			}
			int flag=0;
			ArrayList<Integer> intlist = new ArrayList<Integer>();
			for(int k=0;k<list1.size();k++)
			{
				flag=0;
				ArrayList l = list1.get(k);
				/************************comparision of equals******************************************/
				if(cond.equals("="))
				{
					String str="";
					if(val.contains("'")|| val.contains("\""))
					{
						str = val.substring(1, val.length()-1);
					}
					else
						str= val;
					
					if(((String) l.get(flg)).equalsIgnoreCase(str))
					{
						intlist.add(k);
					}
				}
				
				/************************comparision of >******************************************/
				if(cond.equals(">"))
				{
					int g = -1;
					int v = -1;
					if(coltype.equalsIgnoreCase("int")){
						v= Integer.parseInt(val);
						g=Integer.parseInt((String) l.get(flg));
					}
					else if(coltype.equalsIgnoreCase("short")|| coltype.equalsIgnoreCase("short int")){
						v= Short.parseShort(val);
						g=Short.parseShort((String) l.get(flg));
					}
					else if(coltype.equalsIgnoreCase("long")|| coltype.equalsIgnoreCase("long int")){
						long n= Long.parseLong(val);
						long m = Long.parseLong((String) l.get(flg));
						if(m>n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("float")){
						float n= Float.parseFloat(val);
						float m = Float.parseFloat((String) l.get(flg));
						if(m>n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("double")){
						double n= Double.parseDouble(val);
						double m = Double.parseDouble((String) l.get(flg));
						if(m>n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("byte")){
						byte n= Byte.parseByte(val);
						byte m = Byte.parseByte((String) l.get(flg));
						if(m>n){
							intlist.add(k);
						}
						flag=1;
					}
					else{
						String str="";
						if(val.contains("'")|| val.contains("\""))
						{
							str = val.substring(1, val.length()-1);
						}
						else
							str= val;
						int p=l.get(flg).toString().toLowerCase().compareTo(str.toLowerCase());
						if(p>0)
						{
							intlist.add(k);
						}
						flag=1;
					}

					if(g>v && flag==0)
					{
						intlist.add(k);
					}
					
				}
				
				/************************comparision of <******************************************/
				
				if(cond.equals("<"))
				{
					int g = -1;
					int v = -1;
					if(coltype.equalsIgnoreCase("int")){
						v= Integer.parseInt(val);
						g=Integer.parseInt((String) l.get(flg));
					}
					else if(coltype.equalsIgnoreCase("short")|| coltype.equalsIgnoreCase("short int")){
						v= Short.parseShort(val);
						g=Short.parseShort((String) l.get(flg));
					}
					else if(coltype.equalsIgnoreCase("long")|| coltype.equalsIgnoreCase("long int")){
						long n= Long.parseLong(val);
						long m = Long.parseLong((String) l.get(flg));
						if(m<n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("float")){
						float n= Float.parseFloat(val);
						float m = Float.parseFloat((String) l.get(flg));
						if(m<n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("double")){
						double n= Double.parseDouble(val);
						double m = Double.parseDouble((String) l.get(flg));
						if(m<n){
							intlist.add(k);
						}
						flag=1;
					}
					else if(coltype.equalsIgnoreCase("byte")){
						byte n= Byte.parseByte(val);
						byte m = Byte.parseByte((String) l.get(flg));
						if(m<n){
							intlist.add(k);
						}
						flag=1;
					}
					else{
						String str="";
						if(val.contains("'")|| val.contains("\""))
						{
							str = val.substring(1, val.length()-1);
						}
						else
							str= val;
						
						
						int p=l.get(flg).toString().toLowerCase().compareTo(str.toLowerCase());
						if(p<0)
						{
							intlist.add(k);
						}
						flag=1;
					}

					if(g<v && flag==0)
					{
						intlist.add(k);
					}
					
				}
			}
			
			for(int t1=0; t1<intlist.size();t1++)
			{
				ArrayList result = list1.get(intlist.get(t1));
				
				for(int h=0;h<result.size();h++)
				{
					System.out.print(result.get(h)+"  |  ");
				}
				System.out.println();
			}
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}


	/**
	 *  Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("\t create schema schemaname;    creates the schema with given schemaname");
		System.out.println("\t show schemas;    shows all the schemas");
		System.out.println("\t use schemaname;    uses the given schemaname");
		System.out.println("\t create table tablename ( );   create a new table in current schema.");
		System.out.println("\t show tables;    shows all the tables in the current schema");
		System.out.println("\t version;       Show the program version.");
		System.out.println("\t help;          Show this help information");
		System.out.println("\t exit;          Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*",80));
	}

	/**
	 *  Display the welcome "splash screen"
	 */
	public static void splashScreen() {
		System.out.println(line("*",80));
		System.out.println("Welcome to DavisBaseLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}

	
	
	
	public static void useSchema(String str1)
	{
		
		int flag=0;

		try {
			if((str1)!=null)
			{
				String[] tokens2 = str1.split(" ");
				schemaname= tokens2[1].substring(0, tokens2[1].length()-1);
			}

			RandomAccessFile f= new RandomAccessFile("information_schema.schemata.tbl", "rw");
			for(int j=0;j<f.length(); j++)
			{	
				if(f.getFilePointer()<f.length())
				{
					byte l = f.readByte();
					String str="";
					for(int i=0; i<l; i++)
						str += (char)f.readByte();
					if(schemaname.equalsIgnoreCase(str))
					{
						System.out.println("schema changed");
						flag =1;
					}

				}
			}
			if(flag==0)
				System.out.println("schema not found.. create schema first");

		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	
	
	public static void showShema()
	{

		try {
			RandomAccessFile f= new RandomAccessFile("information_schema.schemata.tbl", "rw");

			try {
				for(int j=0;j<f.length(); j++)
				{	
					if(f.getFilePointer()<f.length())
					{
						byte l = f.readByte();

						for(int i=0; i<l; i++)
							System.out.print((char)f.readByte());
						System.out.print("\n");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	

	public static void select(String input)
	{
		String[]  n = input.split(" ");
		String table_name = n[3];
		ArrayList col_name = new ArrayList<String>();
		ArrayList type_name = new ArrayList<String>();
		try {
			RandomAccessFile f = new RandomAccessFile ("information_schema.columns.tbl","rw");
			int flag=0, flag1=0;
			int num;
			String type="",ty="",y="",colname="";
			for(int j=0;j<f.length(); j++)
			{	
				flag=0;
				flag1=0;
				ty="";
				 colname="";

				if(f.getFilePointer()<f.length()) {
					for(int  k=0; k<3 ;k++){
						String str="";
						byte l = f.readByte();

						for(int i=0; i<l; i++)
							str += (char)f.readByte();
						if(schemaname.equalsIgnoreCase(str)){
							flag=1;
						}
						if(table_name.equalsIgnoreCase(str) && flag==1)
						{
							flag1=1;
						}
						if(k==2 && flag1==1)
						{
							 colname = str;
						}

					}
					 f.readInt();
					
					byte lo= f.readByte();
					for(int i=0; i<lo; i++)
						ty += (char)f.readByte();

					if(flag1==1)
					{
						type= ty;
					}


					byte p= f.readByte();
					for(int j1=0; j1<p; j1++)
						y += (char) f.readByte();

					byte p1= f.readByte();

					for(int j1=0; j1<p1; j1++)
						y += (char) f.readByte();
					
				if(flag1==1){
				col_name.add(colname);
				type_name.add(type);
				}
				}
			}
			
			displayAllRows(table_name,col_name,type_name);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public static void displayAllRows(String tablename,ArrayList col_name, ArrayList type_name)
	{
		System.out.println("-------------------------------------------------------------------------------");
		for(int i=0; i<col_name.size();i++)
		{
			System.out.print(col_name.get(i)+"  |");
		}
		System.out.println("\n-------------------------------------------------------------------------------");
		
		String type="";
		try {
			RandomAccessFile  f = new RandomAccessFile(schemaname+"."+tablename+".tbl","rw");
			
			for(int i=0;i<f.length();i++)
			{
				if(f.getFilePointer()<f.length())
				{
					
					for(int j=0;j<type_name.size();j++)
					{
						 type = (String) type_name.get(j);
							if(type.equalsIgnoreCase("int"))
							{
								System.out.print(f.readInt()+ "  |");
							}
							else if(type.equalsIgnoreCase("long") || type.equalsIgnoreCase("long int"))
							{
								System.out.print(f.readLong()+"  |");
							}
							else if(type.equalsIgnoreCase("short") || type.equalsIgnoreCase("short int"))
							{
								System.out.print(f.readShort()+"  |");
							}
							else if(type.equalsIgnoreCase("float"))
							{
								System.out.print(f.readFloat()+"  |");
							}
							else if(type.equalsIgnoreCase("double"))
							{
								System.out.print(f.readDouble()+"  |");
							}
							else if(type.equalsIgnoreCase("byte"))
							{
								System.out.print(f.readByte()+"  |");
							}
							else 
							{	
								int length = f.readByte();
								String out="";
								for(int g=0; g< length ;g++)
								{
									out+= (char)f.readByte();
								}
								System.out.print(out+"  |");
							}
					}
					
					System.out.println("");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	
	public static void showTables()
	{
		
		try {
			RandomAccessFile f= new RandomAccessFile("information_schema.tables.tbl", "rw");
			int flag=0;
			try {
				for(int j=0;j<f.length(); j++)
				{	
					if(f.getFilePointer()<f.length()) {
						for(int  k=0; k<2 ;k++){
							String str="";
							byte l = f.readByte();

							for(int i=0; i<l; i++)
								str += (char)f.readByte();

							if(flag==1 && !str.isEmpty())
								System.out.println(str);

							if(schemaname.equalsIgnoreCase(str))
							{
								flag=1;
							}
							else
								flag =0;

						}
						f.readLong();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	

	public static void createSchema(String str1)
	{
		try {
			incrementingRows("information_schema", "schemata" );
			if(str1!=null)
			{
				String[] tokens1 = str1.split(" ");
				schemaname= tokens1[2].substring(0, tokens1[2].length()-1);
			}

			RandomAccessFile schemataTableFile= new RandomAccessFile("information_schema.schemata.tbl", "rw");
			schemataTableFile.seek(schemataTableFile.length());
			schemataTableFile.writeByte(schemaname.length());
			schemataTableFile.writeBytes(schemaname);
			System.out.println("schema created successfully");
			schemaname="information_schema";

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static TreeMap<Object, ArrayList> columnIndex = new TreeMap();
	static ArrayList<Integer> list = new ArrayList<Integer>();

	public static void insert(String input)
	{
		String[] p = input.split("[(]");
		String[] r = p[0].split(" ");
		String table_name = r[2];
		String[] values = p[1].split("[,)]");
		ArrayList<ArrayList<String>> colType= new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> checkPN= new ArrayList<ArrayList<String>>();
		ArrayList<String> colName = new ArrayList<String>();
		ArrayList<String> pri = new ArrayList<String>();
		ArrayList<String> notN = new ArrayList<String>();
		String primary = "";
		String notNull = "";
		int flag=0;
		try {
			RandomAccessFile raf = new RandomAccessFile(schemaname+"."+table_name+".tbl" ,"rw");

			/***************code for hash map *************************/
			String[] type = new String[2];
			raf.seek(raf.length());
			int num=(int) raf.getFilePointer();
			checkPN = checkPrimaryNull(table_name);
			colName=checkPN.get(0);
			pri=checkPN.get(3);
			notN=checkPN.get(2);

			if(colName.size()==values.length){
			for(int i=0;i<values.length;i++)
			{
				type =type(table_name,i+1);
				
				
				columnIndex.clear();
				 initializeMap(type[0],type[1] ,table_name);
				
				if(columnIndex.containsKey(values[i]))
				{
					
					if(pri.get(i).equalsIgnoreCase("PRI")){
						flag=1;
						break;
					}
					else{
					raf.seek(raf.length());
					ArrayList l = (ArrayList)columnIndex.get(values[i]);
					l.add(num);	
					}
				}
				else
				{
					list = new ArrayList<Integer>();
					raf.seek(raf.length());
					list.add(num);
					columnIndex.put(values[i], list);
				}
				if(type[1].equalsIgnoreCase("int"))
				{
					raf.writeInt(Integer.parseInt((values[i])));
				}
				else if(type[1].split(" ")[0].equalsIgnoreCase("long"))
				{
					raf.writeLong(Long.parseLong(values[i]));
				}
				else if(type[1].split(" ")[0].equalsIgnoreCase("short"))
				{
					raf.writeShort(Short.parseShort(values[i]));
				}
				else if(type[1].split(" ")[0].equalsIgnoreCase("float"))
				{
					raf.writeFloat(Float.parseFloat(values[i]));
				}
				else if(type[1].equalsIgnoreCase("double"))
				{
					raf.writeDouble(Double.parseDouble(values[i]));
				}
				else if(type[1].equalsIgnoreCase("byte"))
				{
					raf.writeByte(Byte.parseByte(values[i]));
				}
				else 
				{	
					raf.writeByte(values[i].length()-2);
					raf.writeBytes(values[i].substring(1, values[i].length()-1));
				}
				
				RandomAccessFile indexfile = new RandomAccessFile(schemaname+"."+table_name+"."+type[0]+".ndx","rw");
	
				
				for(Entry<Object,ArrayList> entry : columnIndex.entrySet()) {
					Object key = entry.getKey();        
					ArrayList value = entry.getValue(); 
					if(type[1].equalsIgnoreCase("int")||type[1].equalsIgnoreCase("double")||type[1].split(" ")[0].equalsIgnoreCase("short")||type[1].split(" ")[0].equalsIgnoreCase("long")||
							type[1].split(" ")[0].equalsIgnoreCase("float") || type[1].equalsIgnoreCase("byte"))
					{
						
							int number= value.size();
							indexfile.writeInt(Integer.parseInt(key.toString()));
							indexfile.writeInt(number);
							for(int i1=0;i1<number;i1++)
							{
								indexfile.writeInt((int) value.get(i1));
							}
						
					}
					else
					{
						int number1= value.size();
						indexfile.writeByte(key.toString().length());
						indexfile.writeBytes(key.toString());
						indexfile.writeInt(number1);
						for(int i1=0;i1<number1;i1++)
						{
							indexfile.writeInt((int) value.get(i1));
						}
						
					}
					
				}				

			} 
			}
			else
			{	
				System.out.println("please enter all column values");
			}
			if(flag==0){
				if(colName.size()==values.length)
				{
				incrementingRows(schemaname,table_name);
				System.out.println("Row inserted successfully");
				}
			}
			else{
				if(flag==1)
				System.out.println("Duplicate Key not allowed");
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
	@SuppressWarnings("resource")
	private static ArrayList<ArrayList<String>> checkPrimaryNull(String tableName){
		String str;
		String str1;
		String columnName = "";
		String type="";
		int flag1=0;
		int flag=0;
		ArrayList<ArrayList<String>> arrayList=new ArrayList<ArrayList<String>>();
		ArrayList<String> colName = new ArrayList<String>();
		ArrayList<String> typ = new ArrayList<String>();
		ArrayList<String> pri = new ArrayList<String>();
		ArrayList<String> notNull = new ArrayList<String>();
		try{
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for(int i=0;i<columnsTableFile.length();i++){
				str="";
				flag1=0;
				flag=0;
				if(columnsTableFile.getFilePointer()<columnsTableFile.length()){
					for(int k=0;k<3;k++){
						str1="";
						columnName="";
						byte len= columnsTableFile.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)columnsTableFile.readByte();
						if(schemaname.equalsIgnoreCase(str1)){
							flag = 1;
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							flag1=1;
						}
						if(k==2 && flag1==1){
							columnName = str1;
						}
					}
					if(flag1==1){
						columnsTableFile.readInt();

					}
					else{
						columnsTableFile.readInt();
					}
					byte len= columnsTableFile.readByte();
					for(int j=0;j<len;j++){
						str += (char)columnsTableFile.readByte();
					}
					if(flag1==1){
						type = str;

						byte len1= columnsTableFile.readByte();
						String str2="";
						for(int j=0;j<len1;j++)
							str2 += (char) columnsTableFile.readByte();
						byte len2= columnsTableFile.readByte();
						String str3="";
						if(len2 !=0){
							for(int j=0;j<len2;j++)
								str3 += (char) columnsTableFile.readByte();
						}
						else{
							str3="";
						}
						colName.add(columnName);
						typ.add(type);
						notNull.add(str2);
						pri.add(str3);
					}
					else{
						type = str;
						byte len1= columnsTableFile.readByte();
						String str2="";
						for(int j=0;j<len1;j++)
							str2 +=columnsTableFile.readByte();
						byte len2= columnsTableFile.readByte();
						String str3="";
						if(len2 !=0){
							for(int j=0;j<len2;j++)
								str3 +=columnsTableFile.readByte();
						}
						else{
							str3="";
						}

					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		arrayList.add(colName);
		arrayList.add(typ);
		arrayList.add(notNull);
		arrayList.add(pri);
		return arrayList;
	}
	
	public static void incrementingRows(String schemaname,String table_name)
	{
		long rowcount = 0;
		RandomAccessFile f;
		try {
			f = new RandomAccessFile("information_schema.tables.tbl", "rw");
		int flag=0,flag1=0;

		/************** code for increasing table rows ******************/
		for(int j=0;j<f.length(); j++)
		{	
			if(f.getFilePointer()<f.length()) {
				for(int  k=0; k<2 ;k++){
					String str="";
					byte l = f.readByte();

					for(int i=0; i<l; i++)
						str += (char)f.readByte();

					if(schemaname.equalsIgnoreCase(str))
						flag=1;
					else
						flag=0;
					if(table_name.equalsIgnoreCase(str) && k==1)
						flag1=1;
					else
						flag1=0;

				}
				if( flag1==1)
				{
					rowcount = f.readLong();
				}
				else
					f.readLong();
			}	
		}
		flag=0;
		flag1=0;
		RandomAccessFile f1= new RandomAccessFile("information_schema.tables.tbl", "rw");
		for(int j=0;j<f1.length(); j++)
		{	
			if(f1.getFilePointer()<f1.length()) {
				for(int  k=0; k<2 ;k++){
					String str="";
					byte l = f1.readByte();

					for(int i=0; i<l; i++)
						str += (char)f1.readByte();

					if(schemaname.equalsIgnoreCase(str))
						flag=1;
					else
						flag=0;

					if(table_name.equalsIgnoreCase(str) && k==1)
					{
						flag1=1;	
					}
					else 
						flag1=0;
				}
				if(flag1==1)
				{
					f1.writeLong(rowcount+1);
				}
				else
					f1.readLong();
			}
		}
		} 
		catch(Exception e)
		{
			
		}

		/************** end of code for increasing table rows ******************/

	}
	

	public static void initializeMap(String colname2, String type, String tablename2)
	{
		try{
		RandomAccessFile indexfile = new RandomAccessFile(schemaname+"."+tablename2+"."+colname2+".ndx","rw");
		String value1 = "";
		for(int y=0; y<indexfile.length();y++)
		{
			value1="";
			if(indexfile.getFilePointer()<indexfile.length())
			{
			if(type.equalsIgnoreCase("int")||type.equalsIgnoreCase("double")||type.split(" ")[0].equalsIgnoreCase("short")||type.split(" ")[0].equalsIgnoreCase("long")||
					type.split(" ")[0].equalsIgnoreCase("float") || type.equalsIgnoreCase("byte"))
			{
				
					ArrayList l1 = new ArrayList<Integer>();
					int value = indexfile.readInt();
					int size = indexfile.readInt();
					for(int i1=0;i1<size;i1++)
					{
						l1.add(indexfile.readInt());
					}
					columnIndex.put(Integer.toString(value), l1);
				
			}
			else
			{
				ArrayList l1 =  new ArrayList<Integer>();
				int len = indexfile.readByte();
				
				for(int k=0;k<len;k++)
					value1 += (char)indexfile.readByte();
				
				int size = indexfile.readInt();
				for(int i1=0;i1<size;i1++)
				{
					l1.add(indexfile.readInt());
				}
				columnIndex.put(value1, l1);
				
			}
			}
		}
		

		}
		catch(Exception e)
		{
			
		}	
	}
	

	public static String[] type(String tablename1, int col){

		RandomAccessFile f;
		String ty="",y="",type="";
		String[] ret = new String[2];
		String colname="";
		try {
			f = new RandomAccessFile("information_schema.columns.tbl", "rw");

			int flag=0, flag1=0,flag2=0;
			int num;
			

			for(int j=0;j<f.length(); j++)
			{	
				flag=0;
				flag1=0;
				flag2=0;
				ty="";
				 colname="";

				if(f.getFilePointer()<f.length()) {
					for(int  k=0; k<3 ;k++){
						String str="";
						byte l = f.readByte();

						for(int i=0; i<l; i++)
							str += (char)f.readByte();
						if(schemaname.equalsIgnoreCase(str)){
							flag=1;
						}
						if(tablename1.equalsIgnoreCase(str) && flag==1)
						{
							flag1=1;
						}
						if(k==2 && flag1==1)
						{
							 colname = str;
						}

					}
					num= f.readInt();
					if(flag1==1){
						if(num==col)
							flag2=1;
					}
					
					byte lo= f.readByte();
					for(int i=0; i<lo; i++)
						ty += (char)f.readByte();

					if(flag2==1)
					{
						type= ty;
					}


					byte p= f.readByte();
					for(int j1=0; j1<p; j1++)
						y += (char) f.readByte();

					byte p1= f.readByte();

					for(int j1=0; j1<p1; j1++)
						y += (char) f.readByte();
					if(flag2==1)
					{
					ret[0] = colname;
					ret[1] = type;
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return ret;
	}
	
	/*********************************** SPLITTER ***********************************/
	public static void splitter(String command){
		
		String[] call = null;
		String[] n = command.split("[(]");
		int flag=0,j=0;
		String str="";

		str=n[0];
		System.out.println(str);
		String sub = command.substring(str.length()+1, command.length()-1);
		String[] toke =null;
		System.out.println(sub);
		for(int k= j+1; k<n.length; k++)
		{
			toke = sub.split(",");
		}
		call = new String[toke.length+1];
		call[0]=str;
		for(int y=0; y< toke.length ;y++)
		{
			call[y+1] = toke[y];
		}
		createTable(call);

	}
	
	/**************************** CREATE TABLE **********************************/

	public static void createTable(String[] strarray)
	{

		int j=0;
		String strLine="";
		String tablefilename=null;
		String tablename= null;
		try {
			incrementingRows("information_schema","tables");
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for (int h=0; h<strarray.length;h++)   {
				strLine = strarray[h];
				String[] tokens = strLine.split(" ");

				for(int i=0; i<tokens.length; i++)
				{

					if(tokens[i].equalsIgnoreCase("TABLE"))
					{
						tablename = tokens[i+1];
						tablefilename = schemaname+"."+tokens[i+1]+".tbl";
					}

				}  
				int len = tokens.length;

				if(tokens[0].equalsIgnoreCase("")){
					for(int u=0;u<tokens.length-1;u++)
					{
						tokens[u]=tokens[u+1];

					}
					 len = tokens.length-1;
				}

				if(j!=0)
				{
					incrementingRows("information_schema","columns");

					columnsTableFile.seek(columnsTableFile.length());
					columnsTableFile.writeByte(schemaname.length()); 
					columnsTableFile.writeBytes(schemaname);
					columnsTableFile.writeByte(tablename.length()); 
					columnsTableFile.writeBytes(tablename);
					columnsTableFile.writeByte(tokens[0].length()); 
					columnsTableFile.writeBytes(tokens[0]);
					columnsTableFile.writeInt(j); 

					RandomAccessFile indexfile = new RandomAccessFile(schemaname+"."+tablename+"."+tokens[0]+".ndx" , "rw");
					if(len>2)

					{
						if(tokens[1].equalsIgnoreCase("SHORT") || tokens[1].equalsIgnoreCase("LONG"))
						{
							if(tokens[2].equalsIgnoreCase("INT"))
							{
							String combine = tokens[1]+" "+tokens[2];
							columnsTableFile.writeByte(combine.length()); 
							columnsTableFile.writeBytes(combine);

							if(len>3){
								if(tokens[3].equalsIgnoreCase("PRIMARY")){
									columnsTableFile.writeByte("NO".length()); 
									columnsTableFile.writeBytes("NO");
									columnsTableFile.writeByte("PRI".length()); 
									columnsTableFile.writeBytes("PRI");

								}

								if(tokens[3].equalsIgnoreCase("NOT")){
									columnsTableFile.writeByte("NO".length()); 
									columnsTableFile.writeBytes("NO");
									columnsTableFile.writeByte("".length()); 
									columnsTableFile.writeBytes("");

								}
							}
							else
							{
								columnsTableFile.writeByte("YES".length()); 
								columnsTableFile.writeBytes("YES");
								columnsTableFile.writeByte("".length()); 
								columnsTableFile.writeBytes("");
							}
						}
							else
							{
								columnsTableFile.writeByte(tokens[1].length()); 
								columnsTableFile.writeBytes(tokens[1]);

								if(len>2){
									if(tokens[2].equalsIgnoreCase("PRIMARY")){
										columnsTableFile.writeByte("NO".length()); 
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("PRI".length());
										columnsTableFile.writeBytes("PRI");

									}

									if(tokens[2].equalsIgnoreCase("NOT")){
										columnsTableFile.writeByte("NO".length()); 
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("".length()); 
										columnsTableFile.writeBytes("");

									}
								}
								else
								{
									columnsTableFile.writeByte("YES".length()); 
									columnsTableFile.writeBytes("YES");
									columnsTableFile.writeByte("".length()); 
									columnsTableFile.writeBytes("");
								}
							}
						}
						else
						{
							columnsTableFile.writeByte(tokens[1].length()); // COLUMN_TYPE
							columnsTableFile.writeBytes(tokens[1]);


							if(tokens[2].equalsIgnoreCase("PRIMARY")){
								columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
								columnsTableFile.writeBytes("NO");
								columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
								columnsTableFile.writeBytes("PRI");

							}

							if(tokens[2].equalsIgnoreCase("NOT")){
								columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
								columnsTableFile.writeBytes("NO");
								columnsTableFile.writeByte("".length()); // COLUMN_KEY
								columnsTableFile.writeBytes("");

							}

						}
					}
					else{
						columnsTableFile.writeByte(tokens[1].length()); // COLUMN_TYPE
						columnsTableFile.writeBytes(tokens[1]);
						columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
						columnsTableFile.writeBytes("YES");
						columnsTableFile.writeByte("".length()); // COLUMN_KEY
						columnsTableFile.writeBytes("");
					}


				}
				j++;
			}

			RandomAccessFile newtable = new RandomAccessFile(tablefilename,"rw");
			System.out.println("Table successfully created");
			RandomAccessFile tablesTableFile= new RandomAccessFile("information_schema.tables.tbl", "rw");
			tablesTableFile.seek(tablesTableFile.length());
			tablesTableFile.writeByte(schemaname.length());
			tablesTableFile.writeBytes(schemaname);
			tablesTableFile.writeByte(tablename.length());
			tablesTableFile.writeBytes(tablename);
			tablesTableFile.writeLong(0);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}

	/**
	 * @param num The number of newlines to be displayed to <b>stdout</b>
	 */
	public static void newline(int num) {
		for(int i=0;i<num;i++) {
			System.out.println();
		}
	}

	public static void version() {
		System.out.println("DavisBaseLite v1.0\n");
	}


}



