package com.binarybricks.coiny.stories.coinsearch

import CoinSearchContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.binarybricks.coiny.R
import com.binarybricks.coiny.components.historicalchartmodule.CoinSearchPresenter
import com.binarybricks.coiny.network.models.CCCoin
import com.binarybricks.coiny.network.schedulers.SchedulerProvider
import com.binarybricks.coiny.stories.CryptoCompareRepository
import kotlinx.android.synthetic.main.activity_coin_search.*

class CoinSearchActivity : AppCompatActivity(), CoinSearchContract.View {

    private var coinSearchAdapter: CoinSearchAdapter? = null

    companion object {
        @JvmStatic
        fun buildLaunchIntent(context: Context): Intent {
            val intent = Intent(context, CoinSearchActivity::class.java)
            return intent
        }
    }

    private val schedulerProvider: SchedulerProvider by lazy {
        SchedulerProvider.getInstance()
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(schedulerProvider)
    }

    private val coinSearchPresenter: CoinSearchPresenter by lazy {
        CoinSearchPresenter(schedulerProvider, coinRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_search)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvSearchList.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_thin_horizontal)?.let { dividerItemDecoration.setDrawable(it) }

        rvSearchList.addItemDecoration(dividerItemDecoration)


        coinSearchPresenter.attachView(this)

        lifecycle.addObserver(coinSearchPresenter)

        coinSearchPresenter.loadAllCoins()
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvSearchList, errorMessage, Snackbar.LENGTH_LONG)
    }

    override fun onCoinsLoaded(coinList: ArrayList<CCCoin>) {

        coinSearchAdapter = CoinSearchAdapter(coinList)

        rvSearchList.adapter = coinSearchAdapter

        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                coinSearchAdapter?.filter?.filter(filterText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

//        coinSearchAdapter?.setOnSearchItemClickListener(object : ExchangeSearchAdapter.OnSearchItemClickListener {
//            override fun onSearchItemClick(view: View, position: Int, text: String) {
//                intent.putExtra(SEARCH_RESULT, text)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                // tell the calling activity/fragment that we're done deleting this transaction
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
