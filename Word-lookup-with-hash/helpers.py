
from paths import *

import re 

import os


def get_file_size(file_path):
    try:
        with open(file_path, 'r', encoding='latin-1') as file:
            file.seek(0,2)
            file_size = file.tell()
            return file_size
    except OSError:
        print('Something went wrong while getting file size helpers.py')
        exit()

korpus_file_size = get_file_size(korpus_path)

ALPHABET = " abcdefghijklmnopqrstuvwxyzåäö"

U_ALPHABET = " ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ"


c2i = {}

def init_c2i_map():
    '''
        Förberäknar ett värde för varje bokstav i alfabetet:
        Key: bokstav - Value: nummer
    '''
    for i in ALPHABET:
        c2i[i] = ALPHABET.index(i) 

def lazy_hash(letters):
    '''
    Argument: str
    Return: int
    Tar de tre första bokstäverna i ett ord och returnerar hash
    '''
    letters = letters.lower()
    while len(letters) < 3:
        letters += ' '

    return c2i[letters[0]]*900 + c2i[letters[1]]*30 + c2i[letters[2]] 

def upper_lazy_hash(targetWord):

    first3 = targetWord[:3]
    first3 = first3.lower()

    if (first3) == "ööö":
        return lazy_hash(first3)
    if len(first3) == 1:
        return lazy_hash(targetWord) + 30
    if len(first3) > 1:
          return lazy_hash(targetWord) + 1

def get_context(byte_adresses, target_word):
    '''
    Argument: str
    Return: [str, str, str]
    Tar kontext och returnerar pre word post
    '''

    korpus_file = open(korpus_path, 'r', encoding = "latin-1")
    contexts = []

    appetite = 30

    for adress in byte_adresses:
        
        len_tgt = len(target_word)

        # Sök efter ordet genom adress i korpus

        # Om adress precis i starten av fil
        if adress-appetite < 0:
            # kan förfinas
            korpus_file.seek(0)
            pre_tgt = ''
            while korpus_file.tell() < adress-1:
                pre_tgt += korpus_file.read(1)
            tgt = korpus_file.read(len_tgt)            
            # Denna kan förfinas
            post_tgt =  korpus_file.read(appetite)

        # Om adress typ i slutet av faddress
        elif adress+appetite > korpus_file_size:
            korpus_file.seek(adress-appetite)
            pre_tgt = korpus_file.read(appetite)
            tgt = korpus_file.read(len_tgt)            
            # Denna kan förfinas
            post_tgt = ''
            while korpus_file.tell() < adress-1:
                post_tgt += korpus_file.read(1)
        
        # Generella fallet
        else:
            korpus_file.seek(adress-appetite)
            pre_tgt = korpus_file.read(appetite)
            tgt = korpus_file.read(len_tgt)
            post_tgt =  korpus_file.read(appetite)

        ctxt = [pre_tgt, tgt, post_tgt]
        ctxt = [s.replace('\n', ' ') for s in ctxt]

        contexts.append(ctxt)

    korpus_file.close()
    
    return contexts


############################################################

def format_context(context, target_word):
    '''
    Argument: str
    Return: str
    Formaterar en kontextsträng så att den är lättare att läsa och alignar target_word konsekvent.
    '''

    # Dela upp kontexten runt target_word
    before_target = context[0].strip()  # Text före target_word
    after_target = context[2].strip()  # Text efter target_word

    # Se till att vi får exakt 25 tecken på varje sida av target_word
    before_display = before_target[-25:].rjust(25)  # Ta de sista 25 tecknen före target_word och fyll ut om det är kortare
    after_display = after_target[:25].ljust(25)  # Ta de första 25 tecknen efter target_word och fyll ut om det är kortare

    # Justera target_word så att det är centrerat i 25 tecken
    formatted_target = context[1].center(len(target_word) + 2)

    # Bygg hela strängen så att det blir "...text före [target_word centrerat] text efter..."
    return f"...{before_display} {formatted_target} {after_display}..."


def linear_search(lowerbound, upperbound, target_word):
        
        occurrence_file = open(occurrence_path, 'r', encoding='latin-1')
        occurrence_file.seek(lowerbound)
        linecounter = lowerbound
        tmp = occurrence_file.read(1)

        while tmp != '\n': 
            if lowerbound > 0:
                lowerbound -= 1 
            else:
                break
            occurrence_file.seek(lowerbound)
            tmp = occurrence_file.read(1)
        
           
        while linecounter < upperbound:
            line = occurrence_file.readline()

            if not line:
                string = "Word not found"
                print(string)
                exit(0)

            word, occurrences, adress_of_adress = line.split()

            real_addy = occurrence_file.tell()-len(line)

            if word == target_word:
                print(f"Found {target_word} with {occurrences} occurrances") 
                adress_of_adress = int(adress_of_adress)
                occurrences = int(occurrences)
                return word, occurrences,adress_of_adress

            linecounter += 1
        
        occurrence_file.close()
