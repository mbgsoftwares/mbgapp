package com.roamprocess1.roaming4world.roaming4world;

public class SpinnerModel {
    
    private  String countryName="";
    private  String Image=""; 
    private  String Url="";
    private String countryCode=""; 
    /*********** Set Methods ******************/
    public void setCountyName(String countryName)
    {
        this.countryName = countryName;
    }
     
    public void setCountyCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
    public void setImage(String Image)
    {
        this.Image = Image;
    }
     
    public void setUrl(String Url)
    {
        this.Url = Url;
    }
     
    /*********** Get Methods ****************/
    public String getCountryName()
    {
        return this.countryName;
    }
     
    public String getCountryCode()
    {
        return this.countryCode;
    }
    public String getImage()
    {
        return this.Image;
    }
 
    public String getUrl()
    {
        return this.Url;
    }   
}