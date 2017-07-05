sudo apt-get update
sudo apt-get install jsvc git
cd /home
mkdir kikibot
cd kikibot
git init
git remote add origin https://github.com/JulianThijssen/Kikibot.git
git pull origin master
cp build/Kikibot.jar ./Kikibot.jar
cp kikibot /etc/init.d/kikibot
chmod 755 /etc/init.d/kikibot
