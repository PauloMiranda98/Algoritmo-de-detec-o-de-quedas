import math
import os

def verificar_queda(simulacao):

    arquivo = open("simulacao2/" + simulacao, 'r')

    inicio_queda = 0
    posicao_impacto = 0

    max_queda = 0

    fase1 = 0
    fase2 = 0
    fase3 = 0
    fase4 = 0

    somaA = 0

    dic = []

    for linha in arquivo:
        dic.append( linha.split(',') )

    def diferenca(pos):
        posI = pos - 16
        posF = pos

        d = 0.0

        c = []
        for r in range(1, 4):
            c.append(float(dic[posI][r]))
        ant  = math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]) / 9.8

        for i in range(posI+1, posF+2):
            c = []
            for r in range(1, 4):
                c.append(float(dic[i][r]))

            moduloA = math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]) / 9.8
            print(moduloA)

            d = d + math.fabs(moduloA - ant)

            ant = moduloA

        return d

    for pos in range(0, len(dic)):
        vetor_linha = dic[pos]

        timestamp = int(vetor_linha[0])
        c = []

        for i in range(1, 4):
            c.append(float(vetor_linha[i]))

        if(fase1 & fase2 & fase3 & fase4):
            return 1

        if (vetor_linha[4] == '1\n'):
            moduloA = math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]) / 9.8

            if (moduloA > max_queda) & (fase1 == 1) & ((timestamp - inicio_queda) < 400) & (fase2 == 0):
                max_queda = moduloA
                posicao_impacto = pos

            if (fase1 == 1) & (timestamp - inicio_queda >= 400) & (fase2 == 0):
                if(max_queda > 2.6):
                    fase2 = 1
                else:
                    inicio_queda = 0
                    posicao_impacto = 0

                    max_queda = 0

                    fase1 = 0
                    fase2 = 0
                    fase3 = 0
                    fase4 = 0

                    somaA = 0

            if((fase1 == 1) & (timestamp - inicio_queda >= 400) & (fase2 == 1) & (fase3 == 0)):
                if( diferenca( posicao_impacto) > 10):
                    print(diferenca(posicao_impacto))
                    fase3 = 1
                    fase4 = 1

            if(fase1 & fase2 & fase3 & ((timestamp - inicio_queda) < 2000)):
                somaA += math.fabs(c[0]/9.8)+math.fabs(c[1]/9.8)+math.fabs(c[2]/9.8)

            if(fase1 & fase2 & fase3 & ((timestamp - inicio_queda) > 2000 ) ):
                if(somaA < 250):
                    fase4 = 1
                else:
                    fase4 = 0
        else:
            moduloG = (math.sqrt(c[1] * c[1] + c[2] * c[2]) * 180 )/math.pi

            if (moduloG > 200) & (fase1 == 0):
                inicio_queda = timestamp
                fase1 = 1

    return 0

if( verificar_queda("dados_novo1.txt") == 1):
    print("Caiu")

"""for _, _, arquivos in os.walk('C:/Users/Paulo Miranda/PycharmProjects/AlgoritmoDeDetecaoDeQuedas/simulacao2'):

    for simulacao in arquivos:
            print('**************************')
            print(simulacao)
            if (verificar_queda(simulacao) == 1):
                print("caiu")
"""