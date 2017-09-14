# Create a New EB ENV
Create a new ENV ```eb create --cfg croudfunding-dev-env```
Create new RDS 
Save the new configuration ```eb config save --cfg crowdfunding-dev```
Update the Config with the new Database route ```eb config --cfg croudfunding-dev-env```

Switch the IP address for email
Switch the IP address for reCaptcha

Redirects for social
1. Facebook
2. LinkedIn
3. Twitter
4. Google
