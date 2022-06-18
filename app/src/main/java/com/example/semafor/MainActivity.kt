package com.example.semafor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val semafor = Semafor()

        //ulozenie jednotlivych tlacidiel do val
        val pwrOnBtn: Button = findViewById(R.id.power)
        val nmBtn: Button = findViewById(R.id.nightMode)
        val normBtn: Button = findViewById(R.id.normalMode)



        //PowerOn tlacidlo onClick listener
        pwrOnBtn.setOnClickListener {
            semafor.isOn = !semafor.isOn
            if (!semafor.isOn) {
                nmBtn.isEnabled = false
                normBtn.isEnabled = false
                semafor.nuluj()
                nastavObrazky(semafor.cervena, semafor.oranzova, semafor.zelena)
            } else {
                nmBtn.isEnabled = true
                normBtn.isEnabled = true
                //spustenie cyklu
                Thread{chodSemafora(semafor)}.start()
            }
        }

        //nocnyRezim tlacidlo onClick Listener
        nmBtn.setOnClickListener{
            if (semafor.normalnyRezim){
                semafor.normalnyRezim = false
            }
            semafor.nocnyRezim = !semafor.nocnyRezim
        }

        normBtn.setOnClickListener{
            if (semafor.nocnyRezim) {
                semafor.nocnyRezim = false
            }
            semafor.normalnyRezim = !semafor.normalnyRezim
        }



    }

    /**
     * funkcia nastavi obrazky (jednotlive svetelne indikatory)
     * - zapnute = vyfarbene potrebnou farbou
     * - vypnute = sivy obrazok
     * @param cervene - zapne alebo vypne cervene svetlo
     * @param oranzove - zapne alebo vypne oranzove svetlo
     * @param zelene - zapne alebo vypne zelene svetlo
     *
     * mozno mohla brat ako parameter Semafor
     */
    private fun nastavObrazky(cervene: Boolean, oranzove: Boolean, zelene: Boolean) {

        //ulozenie jednotlivych ImageViews do premennych
        val cerveneSveto: ImageView = findViewById(R.id.cerveneSvetlo)
        val oranzoveSvetlo: ImageView = findViewById(R.id.oranzoveSvetlo)
        val zeleneSvetlo: ImageView = findViewById(R.id.zeleneSvetlo)

        //ulozenie id obrazkov do listu 0 - cervena, 1 - oranzova, 2 - zelena, 3 - siva
        val idImg = listOf(R.drawable.cervena, R.drawable.oranzova, R.drawable.zelena, R.drawable.siva)

        //nastavenie cerveneho svetla
        if(cervene) {
            cerveneSveto.setImageResource(idImg[0])
        } else {
            cerveneSveto.setImageResource(idImg[3])
        }

        //nastavenie oranzoveho svetla
        if(oranzove) {
            oranzoveSvetlo.setImageResource(idImg[1])
        } else {
            oranzoveSvetlo.setImageResource(idImg[3])
        }

        //nastavenie zeleneho svetla
        if(zelene) {
            zeleneSvetlo.setImageResource(idImg[2])
        } else {
            zeleneSvetlo.setImageResource(idImg[3])
        }
    }

    /**
     * Hlavna logika semafora, stara sa o jeho nacasovanie a krokovanie
     */
    private fun chodSemafora(semafor: Semafor) {

        while (semafor.isOn) {
            //mozno sa to da spravit jednoduchsie, momentalna implementacia sposobuje bug
            //cyklus sa musi vykonat cely pred vypnutim semafora
            if (semafor.normalnyRezim) {
                //spravi potrebny krok
                semafor.krok(0)
                //nastavi obrazky
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)
                //5 sekund caka
                Thread.sleep(5000)

                semafor.krok(1)
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)

                Thread.sleep(2000)

                semafor.krok(2)
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)

                Thread.sleep(5000)

                semafor.krok(3)
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)

                Thread.sleep(5000)

            } else if (semafor.nocnyRezim) {
                semafor.krok(3)
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)

                Thread.sleep(1000)

                semafor.krok(60)
                nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)

                Thread.sleep(1000)
            }
        }
        //toto tu asi nepotrebujem, riesi to metoda nuluj v Semafore
        /*if (!semafor.isOn) {
            semafor.krok(10)
            nastavObrazky(cervene = semafor.cervena,oranzove = semafor.oranzova,zelene = semafor.zelena)
        }*/
    }

}


/**
 * Trieda semafor
 * uklada si stav jednotlivych svetiel (cervene, oranzove, zelene) stavy = (zapnute / vypnute) -> boolean
 * uklada si stav rezimu v ktorom momentalne pracuje (nocny rezim, normalny rezim) -> boolean
 * pamata si ci je zapnuty (isOn) -> boolean
 */
class Semafor {
    //stavy svetiel
    var cervena: Boolean = false
    var oranzova: Boolean = false
    var zelena: Boolean = false

    //stavy rezimov
    //mozno stacil iba jeden boolean
    var nocnyRezim: Boolean = false
    var normalnyRezim: Boolean = false

    //je zapnuty ?
    var isOn: Boolean = false

    /**
     * nastavi svetla
     * @param krokPointer mod svetiel v ktorom sa maju nachadzat
     */
    fun krok(krokPointer: Int) {
        if (isOn) {
            when(krokPointer) {
                //svieti C
                0 -> {
                    cervena = true
                    oranzova = false
                    zelena = false
                }
                //svieti C a O
                1 -> {
                    cervena = true
                    oranzova = true
                    zelena = false
                }
                //svieti Z
                2 -> {
                    cervena = false
                    oranzova = false
                    zelena = true
                }
                //svieti O
                3 -> {
                    cervena = false
                    oranzova = true
                    zelena = false
                }
                //nic nesvieti
                else -> {
                    cervena = false
                    oranzova = false
                    zelena = false
                }

            }
        }
    }

    /**
     * funkcia sluzi ako setter pre rychlejsiu pracu s atributmi
     * pouziva sa iba pri vypnuti chodu semafora
     */
    fun nuluj() {
        cervena = false
        oranzova = false
        zelena = false
        nocnyRezim = false
        normalnyRezim = false
        isOn = false
    }

}