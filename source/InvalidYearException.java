import java.lang.Exception;

class InvalidYearException extends Exception{ 

	private String msg;

	public InvalidYearException(){
		msg = "Année invalide";
	}  
	public InvalidYearException(String msg){
		this.msg = msg;
	}  

	public String getMessage()
	{
		return msg;
	}

};

class InvalidClassificationException extends Exception
{ 

	private String msg;

	public InvalidClassificationException(){
		msg = "Classification invalide. (voir le dossier doc)";
	}  
	public InvalidClassificationException(String msg){
		this.msg = msg;
	}  

	public String getMessage()
	{
		return msg;
	}

};

class AlreadyInDocException extends Exception
{ 

	private String msg;

	public AlreadyInDocException(){
		msg = "Le document est déjà dans la médiathèque.";
	}  
	public AlreadyInDocException(String msg){
		this.msg = msg;
	}  

	public String getMessage()
	{
		return msg;
	}

};

class InvalidNbPagesException extends Exception
{ 

	private String msg;

	public InvalidNbPagesException(){
		msg = "Nombre de pages invalide.";
	}  
	public InvalidNbPagesException(String msg){
		this.msg = msg;
	}  

	public String getMessage()
	{
		return msg;
	}
};

class NoSaveException extends Exception
{
	private String msg;

	public NoSaveException(){
		msg = "Aucune sauvegarde trouvée";
	}  
	public NoSaveException(String msg){
		this.msg = msg;
	}  

	public String getMessage()
	{
		return msg;
	}
};

