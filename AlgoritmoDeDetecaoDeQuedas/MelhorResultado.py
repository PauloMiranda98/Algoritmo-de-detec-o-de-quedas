import math
import os

def verificar_queda(simulacao):

    arquivo = open("simulacao/" + simulacao, 'r')
    inicio = arquivo.readline().split(': ')[1]
    fim = arquivo.readline().split(': ')[1]

    inicio_queda = 0
    fim_queda = 0

    min_queda = 100.0
    max_queda = 0.0

    maxG = 0.0

    for linha in arquivo:
        vetor_linha = linha.split(',');

        timestamp = int(vetor_linha[0])
        c = []

        for i in range(1, 4):
            c.append(float(vetor_linha[i]))

        if (vetor_linha[4] == '1\n'):
            moduloA = math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]) / 9.8

            if( (min_queda > moduloA) & (moduloA < 0.8) ):
                inicio_queda = timestamp
                min_queda = moduloA

            if ( ((timestamp - inicio_queda) < 260000000) & (moduloA > 2) ):
                if(max_queda < moduloA):
                    max_queda = moduloA
                    fim_queda = 1

            if( ((timestamp - inicio_queda) > 520000000) & ((moduloA > 1.1) | (moduloA < 0.9))
                    & (fim_queda == 1) & ((max_queda - min_queda) < 2.4) ):
                return 0

        else:
            moduloG = (math.sqrt(c[1] * c[1] + c[2] * c[2]) * 180)/ math.pi

            if(moduloG > maxG):
                maxG = moduloG

    if(maxG < 190):
        return 0

    return fim_queda

#verificar_queda("dados_QUEDA_APOIO_FRENTE6.txt")

for _, _, arquivos in os.walk('C:/Users/Paulo Miranda/PycharmProjects/AlgoritmoDeDetecaoDeQuedas/simulacao'):

    for simulacao in arquivos:
        if(verificar_queda(simulacao) == 1):
            print('**************************')
            print(simulacao)
