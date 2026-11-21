import re
import sys
from pathlib import Path

TEMAS = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "values" / "themes.xml"


def color(hexa):
    hexa = hexa.lstrip("#")
    if len(hexa) == 6:
        hexa = "FF" + hexa
    return int(hexa[0:2], 16) / 255, tuple(int(hexa[i:i + 2], 16) for i in (2, 4, 6))


def mezclar(delante, detras):
    alfa, rgb = delante
    return 1.0, tuple(round(a * alfa + b * (1 - alfa)) for a, b in zip(rgb, detras[1]))


def luminancia(c):
    def canal(v):
        v /= 255
        return v / 12.92 if v <= 0.03928 else ((v + 0.055) / 1.055) ** 2.4
    r, g, b = c[1]
    return 0.2126 * canal(r) + 0.7152 * canal(g) + 0.0722 * canal(b)


def ratio(a, b):
    la, lb = luminancia(a), luminancia(b)
    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)


def main():
    texto = TEMAS.read_text(encoding="utf-8")
    print("| Tema | Texto | Texto secundario | La tengo | Falta | Texto sobre principal | Texto sobre acento |")
    print("|---|---|---|---|---|---|---|")
    peor = 99.0
    for nombre, cuerpo in re.findall(r'<style name="(Theme\.JudithFranz[^"]*)"[^>]*>(.*?)</style>', texto, re.S):
        c = {k: color(v) for k, v in re.findall(r'<item name="(tema\w+)">(#[0-9A-Fa-f]+)</item>', cuerpo)}
        inicio, fin = c["temaFondoInicio"], c["temaFondoFin"]
        cuadros = [mezclar(c["temaCuadro"], inicio), mezclar(c["temaCuadro"], fin)]
        campos = [mezclar(c["temaCampo"], q) for q in cuadros]
        fondos = [c["temaTarjeta"], c["temaSuperficie"], inicio, fin] + cuadros + campos
        valores = [min(ratio(c[k], f) for f in fondos) for k in ("temaTexto", "temaTextoSecundario", "temaOk", "temaFalta")]
        valores.append(ratio(c["temaTextoSobrePrincipal"], c["temaPrincipal"]))
        valores.append(ratio(c["temaTextoSobreAcento"], c["temaAcento"]))
        peor = min(peor, *valores)
        print("| %s | %s |" % (nombre, " | ".join("%.2f" % v for v in valores)))
    print()
    print("Ratio minimo: %.2f" % peor)
    return 0 if peor >= 4.5 else 1


if __name__ == "__main__":
    sys.exit(main())
