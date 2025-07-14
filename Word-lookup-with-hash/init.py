'''
I denna fil skapas en fil som innehåller:

1.
index.txt
ord(unikt) antal förekomster adress_till_ordets_första_adreess

2.
adress.txt
adress1(ordets_första_adress) adress2 adress3 adress4 adress5 etc.

När jag kör lokalt på dator tar detta program mindre än 3 min (2min 49 sekunder på min dator).
I labbinstruktionerna så anges det att init.py inte får ta längre tid än 3 min. DÅ är
det 3 minuter utöver tiden att köra tokenizer och sort, so we are cool.

I adress.txt sparar vi max 25 adresser för varje ord.
På så sätt får vi 25 addresser på under 1 min.

Om användaren vill skriva ut fler adresser går vi bara till rawindex.txt och läser av alla adresser för det ordet.
Detta tar längre tid men, tror det är en bra workaround.    
'''

from paths import *
from helpers import *


#####################################################################################################################################
# NEDAN BEARBETAS rawindex.txt
# Vi får ut adresser och antal förekomster för varje ord

# TILLVÄGAGÅNGSSÄTT
# 1) Vi itererar genom rawindex.txt
# 2) Vi skapar en kv-map där vi sparar ordet och antalet förekomster som sedan sparas i occurrences.txt 
# 3) Vi skapar en kv-map där vi sparar ordet och adressen som pekar på adresserna för ordet i adresses.txt

#####################################################################################################################################


try:
    # Open rawindex file
    with open(rawindex_path, "r", encoding="latin-1") as rawindex_file:

        # We want to read through the file and count the occurrences of each word in the file, let's store this in a key-value pair map
        word_occurrences = {}
        word_adresses = {}

        # Read through the file
        for line in rawindex_file:
            # Split the line into words
            data = line.split()
            
            # Check so that line is not empty
            if len(data) > 0:
                # Get the word
                word = data[0]

                # Get the address
                address = data[1]

                # lowercase the word
                word = word.lower()


                # Check if the word is already in the map
                if word in word_occurrences:
                    # Increment the count
                    word_occurrences[word] += 1
                    # Add address, if there is more than 25 adresses, we stop adding them
                    word_adresses[word].append(address)
                else:
                    # Add the word to the map
                    word_occurrences[word] = 1
                    word_adresses[word] = [address]
except FileNotFoundError:
    print("Problem med rawindex-fil i init.py...")
    exit()


#####################################################################################################################################
# NEDAN skapas index.txt och adress.txt

# TILLVÄGAGÅNGSSÄTT
# 1) Vi itererar genom ordet och adresserna
# 2) Vi skriver adresserna till adress.txt där första adressen pekas på av adress i occurrences.txt
# 3) Vi skriver ordet, antalet förekomster och adressen till index.txt


# Adresserna läggs till som
# adress1 adress2 ... adressN andressNext1 andressNext2 ... andressNextN
#####################################################################################################################################


word_adress_in_occurence_ID = 0
word_adress_in_occurence = {}

word_adress_in_adress_ID = 0

try:
    # Open files for writing addresses and occurrences
    with open(adresses_path, "w", encoding="latin-1") as adresses_file, \
        open(occurrence_path, "w", encoding="latin-1") as occurrences_file:

        # Iterate through all words and addresses
        for word, addresses in word_adresses.items():
            
            # Convert list of addresses to string
            addresses_str = ' '.join(addresses) + ' '
            
            # Save the current pointer in the address file (using tell())

            # Write all addresses to the file
            adresses_file.write(addresses_str)
            
            word_adress_in_occurence[word] = word_adress_in_occurence_ID

            occurrences_file_content = f"{word} {word_occurrences[word]} {word_adress_in_adress_ID}\n"
            
            # Write the word and occurrences along with the address pointer to the occurrences file
            occurrences_file.write(occurrences_file_content)

            word_adress_in_occurence_ID += len(occurrences_file_content)
            word_adress_in_adress_ID += len(addresses_str) 
except:
    print("problem med att öppna addresses.txt och occurrence.txt")


#####################################################################################################################################
# NEDAN görs konkordansfilen
# Detta går nu mycket snabbare eftersom vi har en ny indexfil som innehåller unika ord utan upprepningar
#####################################################################################################################################

# Anpassa gammal kod till ny implementattion


# I konkordansfilen vill vi på plats lazy_hash(word)*4 skriva adressen för ordet i occurrences.txt
# adresserna för varje ord i occurrences.txt är sparade i en kv-map

found_word = []
found_address = []

init_c2i_map()

# Antal bytes att läsa i varje block
BLOCK_SIZE = 1024 * 1024  # 1 MB

try:
    # Öppna filerna
    index_file = open(occurrence_path, encoding="latin-1")


    # Öppna filen på
    konkordans_file = open(konkordans_path, 'w+b')
except:
    print("fel med att öppna filer init.py index eller konkordans")
    exit()

# Initiera variabler
current_word = ""
last_word = ""
buffer = ""
file_position = 0  # Startposition i filen

while True:
    chunk = index_file.read(BLOCK_SIZE)
    if not chunk:
        break
    
    buffer += chunk  # Lägg till den inlästa delen till buffern
    
    # Dela upp blocket i rader
    lines = buffer.splitlines(keepends=True)  
    
    # Behåll den sista raden i buffern för nästa block
    if not chunk.endswith('\n'):
        buffer = lines.pop()
    else:
        buffer = ""

    # Iterera genom raderna och hantera ord och adresser
    for line in lines:
        parts = line.split()
        if len(parts) > 0:
            current_word = parts[0][:3]  # Ta de första 3 bokstäverna av ordet
            if current_word != last_word:
                found_word.append(current_word)
                found_address.append(file_position)
                last_word = current_word
        # Uppdatera positionen baserat på längden av raden
        file_position += len(line)

# Stäng indexfilen
index_file.close()


# Skriv adresserna i konkordansfilen
for word, address in zip(found_word, found_address):
    konkordans_file.seek(int(lazy_hash(word)) * 4)
    konkordans_file.write(address.to_bytes(4, byteorder='little'))

# Stäng konkordansfilen
konkordans_file.close()

