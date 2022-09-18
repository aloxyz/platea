package alo;


public class App

{
    public static void main( String[] args) throws Exception
    {

        DockerController
            .get(
                "containers",
                "718ee08de89dcff8e9e132e60e40327b94227de9880c1d1cb36a5b0a0f514e11");
            
        DockerController
        .get(
            "images",
            "");
    }
}