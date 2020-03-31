CREATE TABLE IF NOT EXISTS HEARTBEAT ( 
   id IDENTITY NOT NULL PRIMARY KEY , 
   time VARCHAR(100) NOT NULL, 
   type VARCHAR(100) NOT NULL, 
   category VARCHAR(100) NOT NULL, 
   is_write BOOLEAN DEFAULT FALSE, 
   project VARCHAR(100) NOT NULL, 
   branch VARCHAR(100) NOT NULL, 
   language VARCHAR(100) NOT NULL, 
   dependencies VARCHAR(100) NOT NULL, 
   lines VARCHAR(100) NOT NULL, 
   lineno VARCHAR(100) NOT NULL, 
   cursorpos VARCHAR(100) NOT NULL, 
   user_agent VARCHAR(100) NOT NULL, 
   tokenid VARCHAR(30) NOT NULL, 
   eventDate TIMESTAMP 
   
);
COMMIT;