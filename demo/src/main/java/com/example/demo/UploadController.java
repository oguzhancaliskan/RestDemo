package com.example.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UploadController {
	public String filename;
	
   @RequestMapping(value = "/upload", method = RequestMethod.POST, 
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   
   public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
	  filename = "./src/main/resources/"+file.getOriginalFilename();
      File convertFile = new File(filename);
      convertFile.createNewFile();
      FileOutputStream fout = new FileOutputStream(convertFile);
      fout.write(file.getBytes());
      fout.close();
      return "File is upload successfully";
   }    
   
   @RequestMapping(value = "/upload/{id}")
   public ResponseEntity<Object> getProduct(@PathVariable("id") String id) throws IOException {
	   BufferedReader brTest = null;
	   String text = null;
	   try {
		   brTest = new BufferedReader(new FileReader(filename));
		do {
			text = brTest.readLine();
			if(text == null)
				break;
			if(text.contains(id))
				break;
		}while(text != null);
	} catch (FileNotFoundException e) {		
		e.printStackTrace();
	} finally {            
        if(brTest!=null)
    	   brTest.close();
    }	  
	
       return new ResponseEntity<>(text, HttpStatus.OK);
   }
   
   @RequestMapping(value = "/upload/{id}", method = RequestMethod.DELETE)
   public ResponseEntity<Object> delete(@PathVariable("id") String id) throws IOException { 
	   BufferedReader reader = null;
	   BufferedWriter writer = null;
	   String text = null;
	   boolean isChanged = false;
		try {
			reader = new BufferedReader(new FileReader(filename));
			writer = new BufferedWriter(new FileWriter("./src/main/resources/temp.txt"));			
			do {
				text = reader.readLine();				
				if(text == null)
					break;
				if(text.contains(id)) {
					isChanged = true;
					continue;
				}
				
				writer.write(text);
				writer.newLine();				
			} while(text != null);
			writer.flush();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} finally {		        
		    if(reader!=null)
		    	reader.close();
		    if(writer!=null)
		    	writer.close();
		}      
		
		if(isChanged) {
			try {
				reader = new BufferedReader(new FileReader("./src/main/resources/temp.txt"));
				writer = new BufferedWriter(new FileWriter(filename));			
				do {
					text = reader.readLine();				
					if(text == null)
						break;				
					
					writer.write(text);
					writer.newLine();				
				} while(text != null);
				writer.flush();
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
			} finally {		           
			    if(reader!=null)
			       reader.close();
			    if(writer!=null)
			    	writer.close();
			}    
		}
	      
      return new ResponseEntity<>("Record is deleted successsfully", HttpStatus.OK);
   }
  
}

