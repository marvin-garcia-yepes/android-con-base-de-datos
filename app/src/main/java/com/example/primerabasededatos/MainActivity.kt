package com.example.primerabasededatos

import android.content.ContentValues
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var txtCodigo: EditText? = null
    var txtEstudiante: EditText? = null
    var txtInstitucion: EditText? = null
    var txtCurso: EditText? = null
    var txtBuscarPor: EditText? = null
    var spBuscar: Spinner? = null
    var tlInstitucion: TableLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtCodigo = findViewById(R.id.txtCodigo)
        txtEstudiante = findViewById(R.id.txtEstudiante)
        txtInstitucion = findViewById(R.id.txtInstitucion)
        txtCurso = findViewById(R.id.txtCurso)
        tlInstitucion = findViewById(R.id.tlInstitucion)
        txtBuscarPor = findViewById(R.id.txtBuscarPor)
        spBuscar = findViewById(R.id.spBuscar)
        var listaCampo =
            arrayOf("Seleccione el campo deseado", "codigo", "estudiante", "institucion", "curso")
        var adaptador: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaCampo)
        spBuscar?.adapter = adaptador
    }

    fun insertar(view: View) {
        var constan = SQL(this, "colegio", null, 1)
        var dataBase = constan.writableDatabase
        var codigo = txtCodigo?.text.toString()
        var estudiante = txtEstudiante?.text.toString()
        var institucion = txtInstitucion?.text.toString()
        var curso = txtCurso?.text.toString()

        if (!codigo.isEmpty() && !estudiante.isEmpty() && !institucion.isEmpty() && !curso.isEmpty()) {
            var registro = ContentValues()
            registro.put("codigo", codigo)
            registro.put("estudiante", estudiante)
            registro.put("institucion", institucion)
            registro.put("curso", curso)
            dataBase.insert("usuarios", null, registro)
            txtCodigo?.setText("")
            txtEstudiante?.setText("")
            txtInstitucion?.setText("")
            txtCurso?.setText("")
            Toast.makeText(this, "Se ha registrado un nuevo estudiante", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Los campos son obligatorios", Toast.LENGTH_LONG).show()
        }
        dataBase.close()
    }

    fun eliminar(view: View) {
        val constan = SQL(this, "colegio", null, 1)
        val dataBase = constan.writableDatabase
        val codigo = txtCodigo?.text.toString()
        if (!codigo.isEmpty()) {
            val constantante = dataBase.delete("usuarios", "codigo='" + codigo + "'", null)
            if (constantante > 0) {
                Toast.makeText(this, "El estudiante fue eliminado con exito", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "No se pudo encontrar el estudiante", Toast.LENGTH_LONG).show()
            }
            txtCodigo?.setText("")
            txtEstudiante?.setText("")
            txtInstitucion?.setText("")
            txtCurso?.setText("")
        } else {
            Toast.makeText(this, "El campo debe tener informacion", Toast.LENGTH_LONG).show()
        }
        llenarTabla()
    }

    fun editar(view: View) {
        val constan = SQL(this, "colegio", null, 1)
        val dataBase = constan.writableDatabase
        val codigo = txtCodigo?.text.toString()
        val estudiante = txtEstudiante?.text.toString()
        val institucion = txtInstitucion?.text.toString()
        val curso = txtCurso?.text.toString()
        if (!codigo.isEmpty() && !estudiante.isEmpty() && !institucion.isEmpty() && !curso.isEmpty()) {
            var registro = ContentValues()
            registro.put("codigo", codigo)
            registro.put("estudiante", estudiante)
            registro.put("institucion", institucion)
            registro.put("curso", curso)
            val constantante = dataBase.update("usuarios", registro, "codigo=$codigo", null)
            if (constantante > 0) {
                Toast.makeText(this, "El estudiante se actualizo con exito", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "No se encontro el estudiante", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Informacion encompleta", Toast.LENGTH_LONG).show()
        }
        llenarTabla()
    }

    fun llenarTabla() {
        tlInstitucion?.removeAllViews()
        val constan = SQL(this, "colegio", null, 1)
        val dataBase = constan.writableDatabase
        val buscarPor = txtBuscarPor?.text.toString()
        val listaBuscarPor = spBuscar?.selectedItem.toString()
        var sql = ""
        if (!buscarPor.isEmpty()) {
            if (listaBuscarPor == "Codigo") {
                sql =
                    "select codigo,estudiante,institucion,curso from usuarios where codigo='$buscarPor'"
            } else if (listaBuscarPor == "Estudiante") {
                sql =
                    "select codigo,estudiante,institucion,curso from usuarios where estudiante like'%$buscarPor%'"
            } else if (listaBuscarPor == "Institucion") {
                sql =
                    "select codigo,estudiante,institucion,curso from usuarios where institucion like'%$buscarPor%'"
            } else if (listaBuscarPor == "Curso") {
                sql =
                    "select codigo,estudiante,institucion,curso from usuarios where curso='$buscarPor'"
            } else {
                sql = "select codigo,estudiante,institucion,curso from usuarios"
            }

            val fila = dataBase.rawQuery(sql, null)
            fila.moveToFirst()
            do {
                val registro =
                    LayoutInflater.from(this).inflate(R.layout.item_table_layout_pn, null, false)
                val tvCodigo = registro.findViewById<View>(R.id.tvCodigo) as TextView
                val tvEstudiante = registro.findViewById<View>(R.id.tvEstudiante) as TextView
                val tvInstitucion = registro.findViewById<View>(R.id.tvInstitucion) as TextView
                val tvCurso = registro.findViewById<View>(R.id.tvCurso) as TextView
                tvCodigo.setText(fila.getString(0))
                tvEstudiante.setText(fila.getString(1))
                tvInstitucion.setText(fila.getString(2))
                tvCurso.setText(fila.getString(3))
                tlInstitucion?.addView(registro)
            } while (fila.moveToNext())
        }
    }

    fun clickRegiatros(view: View) {
        resetColor()
        view.setBackgroundColor(Color.GREEN)
        val registro = view as TableRow
        val controlCodigo = registro.getChildAt(0) as TextView
        val codigo = controlCodigo.text.toString()
        val constan = SQL(this, "colegio", null, 1)
        val database = constan.writableDatabase
        if (!codigo.isEmpty()) {
            val fila = database.rawQuery(
                "select codigo,estudiante,institucion,curso from usuarios where codigo='$codigo'",
                null
            )
            if (fila.moveToFirst()) {
                txtCodigo?.setText(fila.getString(0))
                txtEstudiante?.setText(fila.getString(1))
                txtInstitucion?.setText(fila.getString(2))
                txtCurso?.setText(fila.getString(3))
            } else {
                txtCodigo?.setText("")
                txtEstudiante?.setText("")
                txtInstitucion?.setText("")
                txtCurso?.setText("")
                Toast.makeText(this, "No se encontraron registros", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun resetColor() {
        for (i in 0..tlInstitucion!!.childCount) {
            val registros = tlInstitucion?.getChildAt(i)
            registros?.setBackgroundColor(Color.WHITE)
        }
    }

    fun clickLLenarTabla(view: View) {
        llenarTabla()
    }
}