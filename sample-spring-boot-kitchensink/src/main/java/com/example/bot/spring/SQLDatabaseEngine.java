package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		String result = null;		
		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT keyword, response, hit FROM chatting;");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				String name = rs.getString(1);			
				if(text.toLowerCase().contains(name.toLowerCase())){
					result = rs.getString(2);
					int num = rs.getInt(3); num++;
					
					String query = "UPDATE chatting SET hit = ? where keyword = ?;";
				    PreparedStatement hitschange = con.prepareStatement(query);
				    hitschange.setInt(1,num);
				    hitschange.setString(2,name); 
				    hitschange.executeUpdate();
				    result = result + " hits: "+num;
				    hitschange.close();															
					break;					
				}	
			}
			rs.close();
			stmt.close();
			con.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");		
	}		
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
