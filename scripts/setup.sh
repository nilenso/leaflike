# install java
if ! [ -x "$(which java)" ]; then
    echo "Installing java..."
    apt-get install oracle-java8-installer
fi

# install lein
if ! [ -x "$(which lein)" ]; then
    echo "Installing lein..."
    mkdir ~/bin
    wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -o ~/bin/lein
    chmod +x ~/bin/lein
fi

# install postgresql
# TODO: set pasword

apt-get install postgresql postgresql-contrib
createuser leaflike_user -S -l -d
createdb -U leaflike_user leaflike
