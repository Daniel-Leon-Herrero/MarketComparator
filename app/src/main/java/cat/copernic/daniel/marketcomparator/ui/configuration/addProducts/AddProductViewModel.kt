package cat.copernic.daniel.marketcomparator.ui.configuration.addProducts

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.daniel.marketcomparator.R
import cat.copernic.daniel.marketcomparator.domain.data.network.Repo
import cat.copernic.daniel.marketcomparator.getMercados
import cat.copernic.daniel.marketcomparator.model.Mercado
import cat.copernic.daniel.marketcomparator.model.PreciosSupermercados
import cat.copernic.daniel.marketcomparator.model.ProductsDTO
import com.example.android.eggtimernotifications.util.sendNotification
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Double.parseDouble

class AddProductViewModel : ViewModel() {
    var options: Array<String> = arrayOf("Blau", "Verd", "Groc", "Marró", "Gris")
    var optionsMarket: MutableList<String> = mutableListOf()
    var numid: Long = 0
    var idProducto: String
    var listPrices: MutableList<PreciosSupermercados> = mutableListOf()
    var product: ProductsDTO = ProductsDTO("", "", mutableListOf(), "", 0, "")
    private val repo = Repo()

    private var database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("products")

    private lateinit var context: Context

    init {
        idProducto = "$numid" + "product"
        getLastIDFirebase()
    }

    fun insertarDatosBBDD() {
        viewModelScope.launch(Dispatchers.IO) {
            database.child(idProducto).setValue(product)
                .addOnSuccessListener {
                    showPositiveProductRegisterAlert()
                    val notificationManager = ContextCompat.getSystemService(
                        context,
                        NotificationManager::class.java
                    ) as NotificationManager
                    var nombre: String = product.nombreProducto
                    notificationManager.sendNotification(
                        context.getString(R.string.notificationText) + nombre,
                        context
                    )
                    listPrices.clear()

                }.addOnFailureListener {
                    showNegativeProductRegisterAlert()
                }
        }
    }

    fun setContext(con: Context) {
        context = con
    }


    fun incrementarid() {
        numid++
        idProducto = "$numid" + "product"
    }

    fun getLastIDFirebase() {
        viewModelScope.launch(Dispatchers.Main) {
            val querry = FirebaseDatabase.getInstance().reference.child("products")

            withContext(Dispatchers.IO) {
                querry.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (products in snapshot.children) {
                            if (numid <= getNumericValues(products.key.toString()).toLong()) {
                                numid = getNumericValues(products.key.toString()).toLong()
                                incrementarid()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    fun getNumericValues(cadena: String): String {

        val sb = StringBuilder()

        for (i in cadena.indices) {
            var numeric = true
            try {
                val num = parseDouble(cadena[i].toString())
            } catch (e: NumberFormatException) {
                numeric = false
            }

            if (numeric) {
                //Valor numeric
                sb.append(cadena[i].toString())
            } else {
                //Valor no numeric.
            }

        }

        return sb.toString();
    }

    fun fetchMarketData(): LiveData<MutableList<Mercado>> {
        val mutableData = MutableLiveData<MutableList<Mercado>>()
        repo.getMarketsData().observeForever {
            mutableData.value = it
        }

        return mutableData
    }

    fun setValuesSpinnerMarket() {
        optionsMarket.clear()
        for (markets in fetchMarketData().value!!) {
            optionsMarket.add(markets.nombreMercado)
        }
    }

    private fun showPositiveProductRegisterAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.rightMessage))
        builder.setMessage(context.getString(R.string.verifyC))
        builder.setPositiveButton(context.getString(R.string.acceptMessage), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showNegativeProductRegisterAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.wrongMessage))
        builder.setMessage(context.getString(R.string.verifyF))
        builder.setPositiveButton(context.getString(R.string.acceptMessage), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}