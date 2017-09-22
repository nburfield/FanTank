# Create a New EB ENV
Create a new ENV ```eb create --cfg crowdfunding-dev```
Create new RDS 
Save the new configuration ```eb config save --cfg crowdfunding-dev```
Update the Config with the new Database route ```eb config --cfg crowdfunding-dev```

Switch the IP address for email
Switch the IP address for reCaptcha
Switch the IP address for FundAmerica Webhook

Redirects for social
1. Facebook
2. LinkedIn
3. Twitter
4. Google
