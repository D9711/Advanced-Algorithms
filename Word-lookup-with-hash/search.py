'''
Sökprogrammet ska inte läsa igenom hela texten och får inte använda speciellt mycket internminne. 
Internminnesbehovet ska inte växa med antalet distinkta ord i den ursprungliga texten (med andra 
ord ha konstant internminneskomplexitet). Ni ska därför använda latmanshashning (se föreläsning 2) 
som datastruktur. Vid redovisningen ska ni kunna motivera att internminneskomplexiteten är konstant 
för sökprogrammet.

Vårt fall:
Minneskomplexiteten växer med antalet förekomster i texten. Inte antalet distinka ord.

Å andra sidan är den interna minneskomplexiteten inte konstant.
Behovet för minnet växer med antalet förekomster i texten.
'''

import time
from helpers import *
from paths import *

from math import floor

import sys

init_c2i_map()
import re

#####################################################################################################################################
# NEDAN FÅR VI INDEX FÖR FÖRSTA FÖREKOMSTEN AV EN TREBOKSTAVSKOMBINATION I OCCURRENCE.TXT
#####################################################################################################################################

# Get word from user
target_word = (sys.argv[1]).lower().strip()

target_hash = lazy_hash(target_word) 
upper_bound_hash = upper_lazy_hash(target_word)


# Get position from konkordans file
try:
    with open(konkordans_path, 'r+b') as konkordans_file:
        konkordans_file.seek(target_hash * 4)
        w_prefix_1 = int.from_bytes(konkordans_file.read(4), byteorder='little')

        # This is used for linear search for words with more than 25 occurrences
        adress_for_easy_search = w_prefix_1
        
        konkordans_file.seek(upper_bound_hash * 4)

        w_prefix_2 = int.from_bytes(konkordans_file.read(4), byteorder='little')
except FileNotFoundError:
    print("Konkordansfilproblem.")
    exit(1) 


#####################################################################################################################################
# NEDAN HITTAR VI ADRESSEN FÖR ORDETS ADRESSER SAMT ANTAL FÖREKOMSTER

# TILLVÄGAGÅNGSSÄTT:
# 1. Öppna occurrence.txt
# 2. Gå till adressen för ordet
# 3. Linjärsökning för att hitta ordet, ska bytas ut mot binärsökning sen
#####################################################################################################################################


# try: 
#     with open(occurrence_path, 'r', encoding='latin-1') as occurrence_file:
#         occurrence_file.seek(w_prefix_1)
        
#         while True:
            
#             line = occurrence_file.readline()

#             if not line:
#                 print("Word not found hej")
#                 exit(0)
    
#             word, occurrences, adress_of_adress = line.split()

#             real_addy = occurrence_file.tell()-len(line)

#             if word == target_word:
#                 print(f"Found {target_word} {occurrences} times at adress {real_addy} in occurrane.txt") 
#                 adress_of_adress = int(adress_of_adress)
#                 break
                
# except FileNotFoundError:
#     print("Occurrencefilproblem.")
#     exit(1)

#####################################################################################################################################
# Binary search test 

binary_res = 0

try: 
    with open(occurrence_path, 'r', encoding='latin-1') as occurrence_file:
        # byte_adresser som är i ordningen j och j+1 i occurrance.txt
        i = w_prefix_1
        j = w_prefix_2
        m = i

        while j-i > 1000:
            
            m = floor((i+j)/2)


            occurrence_file.seek(m)
            
            # Hitta närmaste newline char
            
            tmp = occurrence_file.read(1)

            # backtrack            
            while tmp != '\n': 
                m -= 1
                occurrence_file.seek(m)
                tmp = occurrence_file.read(1)
            
            line = occurrence_file.readline()
            w, o, adress_of_adress = line.split()
            
            if w <= target_word:
                i = m
            else:
                j = m

        binary_res = m

        
    
except FileNotFoundError:
    print("Occurrencefilproblem.")
    exit(1)


try: 
    with open(occurrence_path, 'r', encoding='latin-1') as occurrence_file:
        
        occurrence_file.seek(binary_res)
        tmp = occurrence_file.read(1)

        # backtrack            
        while tmp != '\n': 
            if binary_res > 0:
                binary_res -= 1 
            else:
                break
            occurrence_file.seek(binary_res)
            tmp = occurrence_file.read(1)
     

        line = occurrence_file.readline()
        word, occurrences, adress_of_adress = line.split()

        if word == target_word:
            print(f'Found {word} with {occurrences} occurrances')
            adress_of_adress = int(adress_of_adress)
                
        if word < target_word:
            try:
                word, occurrences, adress_of_adress = linear_search(binary_res, j,target_word)
            except Exception:
                print("Word not found")
                exit()
        if word > target_word:
            try:
                word, occurrences, adress_of_adress = linear_search(i ,binary_res, target_word)
            except Exception:
                print("Word not found")
                exit()

                
except FileNotFoundError:
    print("Occurrencefilproblem.")
    exit(1)


#####################################################################################################################################
# NEDAN HITTAR VI ADRESSERNA FÖR ORDET

# TILLVÄGAGÅNGSSÄTT:
# Om ordet har mindre än 26 förekomster så skrivs alla ut, annars skrivs de 25 första ut
# Om ordet har mer än 25 förekomster så frågar programmet om allt ska skrivas ut - om jag så letar vi efter alla adresser i rawindex.txt 
# Det sistnämnda sker med linjärsökning
#####################################################################################################################################






try:
    with open(adresses_path, 'r', encoding='latin-1') as adresses_file:
        
        try:
            adresses_file.seek(int(adress_of_adress))
        except Exception as e:
            print(f'Cant read {adress_of_adress}')
            exit()


        occurrences = int(occurrences)



        
        if occurrences > 25:
            adresses = []
            adress_counter = 0
            s = adresses_file.read(1)
            adress = s
            
            while adress_counter < 25:
                s = adresses_file.read(1)
                if s == ' ':
                    adresses.append(int(adress))
                    adress_counter += 1
                    adress = ''
                if s != ' ':
                    adress += s
            
            contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
            for context in contexts:
                print(context)

            user_input = str(input(f'Display {occurrences-25} additional contexts? (y/n): '))
            
            if user_input == 'y':
                adresses = []
                s = adresses_file.read(1)
                adress = s
                while adress_counter < occurrences:
                    s = adresses_file.read(1)
                    if s == ' ':
                        adresses.append(int(adress))
                        adress_counter += 1
                        adress = ''
                    if s != ' ':
                        adress += s
                contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
                for context in contexts:
                    print(context)
            else:
                exit()
            


        else:
            data = adresses_file.read(occurrences*14).split()
            adresses = data[:occurrences]
            adresses = [int(adress) for adress in adresses]
            contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
            for context in contexts:
                print(context)
       


        # if occurrences > 25:    
        #     data = adresses_file.read(25*14).split()
        #     adresses = data[:25]
        #     adresses = [int(adress) for adress in adresses]
        #     contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
        #     for context in contexts:
        #         print(context)

        #     user_input = str(input(f'Display {occurrences-25} additional contexts? (y/n): '))
            
        #     if user_input == 'y':
        #         data += adresses_file.read((occurrences-25)*14).split()
        #         adresses = data[25:occurrences-25]
        #         adresses = [int(adress) for adress in adresses]
        #         contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
                
        #         for context in contexts:
        #             print(context)
        #     else :
        #         print("Exiting")
        #         exit(0)
        
        # else:
        #     data = adresses_file.read(occurrences*14).split()
        #     adresses = data[:occurrences]
        #     adresses = [int(adress) for adress in adresses]
        #     contexts = [format_context(context, target_word) for context in get_context(adresses, target_word)]
        #     for context in contexts:
        #         print(context)
    
except FileNotFoundError:
    print("Rawindexfilproblem.")
    exit(1)

#####################################################################################################################################
# NEDAN HITTAR VI KONTEXTERNAS FÖR ORDET
#####################################################################################################################################

#####################################################################################################################################
