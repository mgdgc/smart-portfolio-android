package com.mgchoi.smartportfolio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.adapter.PortfolioEditAdapter
import com.mgchoi.smartportfolio.adapter.PortfolioEditRequest
import com.mgchoi.smartportfolio.adapter.ProfileEditRequest
import com.mgchoi.smartportfolio.databinding.ActivityPortfolioManageBinding
import com.mgchoi.smartportfolio.databinding.LayoutPortfolioWriteBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.tool.CoverImageHelper
import com.mgchoi.smartportfolio.tool.ProfileImageHelper

class PortfolioManageActivity : AppCompatActivity(), ProfileEditRequest, PortfolioEditRequest {

    companion object {
        const val EXTRA_MEMBER = "member"
    }

    private lateinit var binding: ActivityPortfolioManageBinding
    private lateinit var adapter: PortfolioEditAdapter
    private lateinit var member: Member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarManage)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initIntentData()
        initRecyclerView()
        initView()
        initData()

    }

    private fun initIntentData() {
        val memberId = intent.getIntExtra(EXTRA_MEMBER, -1)
        if (memberId == -1) this.finish()

        val dao = MemberDAO(this)
        val member = dao.select(memberId)

        if (member == null) {
            Toast.makeText(this, R.string.portfolio_manage_error, Toast.LENGTH_LONG)
                .show()
            this.finish()
        } else {
            this.member = member
        }
    }

    private fun initView() {
        // 포트폴리오 추가 Floating Action Button OnClickListener 설정
        binding.fabPortfolioManage.setOnClickListener {
            showPortfolioWriteDialog(null) {
                adapter.data.add(it)
                adapter.notifyItemInserted(adapter.data.size)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = PortfolioEditAdapter(this, member)
        adapter.portfolioEditRequest = this
        adapter.profileEditRequest = this

        binding.rvManage.adapter = adapter
        binding.rvManage.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        val dao = PortfolioDAO(this)
        adapter.data.addAll(dao.selectAll(member.id))
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onImageEditRequest() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        profileImageActivityResult.launch(intent)
    }

    private val profileImageActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val uri = it.data?.data
        if (uri != null) {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }

            val manager = ProfileImageHelper(this)
            val filename = manager.save(bitmap)
            member.image = filename

            val dao = MemberDAO(this)
            dao.update(member)

            adapter.notifyItemChanged(0)
        }

    }

    override fun onNameEditRequest() {
        // AlertDialog에 설정할 EditText
        val editText = EditText(this)
        // 기존 데이터 설정
        editText.setText(member.name)

        // 이름 변경 AlertDialog 보여주기
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.portfolio_edit_name_title)
            .setView(editText)
            .setPositiveButton(R.string.confirm) { d, _ ->
                val newValue = editText.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    member.name = newValue
                    val dao = MemberDAO(this)
                    dao.update(member)
                    adapter.notifyItemChanged(0)
                }
                d.dismiss()
            }
            .setNegativeButton(R.string.cancel) { d, _ ->
                d.dismiss()
            }
            .show()
    }

    override fun onViewStyleChange(newValue: ViewStyle) {
        member.viewStyle = newValue
        val dao = MemberDAO(this)
        dao.update(member)
    }

    override fun onEditRequest(index: Int, position: Int, portfolio: Portfolio) {
        showPortfolioWriteDialog(portfolio) {
            adapter.data[index] = it
            adapter.notifyItemChanged(position)
        }
    }

    override fun onDeleteRequest(index: Int, position: Int, portfolio: Portfolio) {
        val dao = PortfolioDAO(this)
        dao.delete(portfolio.id)

        adapter.data.removeAt(index)
        adapter.notifyItemRemoved(position)
    }

    private fun showPortfolioWriteDialog(portfolio: Portfolio?, onFinish: ((Portfolio) -> Unit)) {
        val writeBinding = LayoutPortfolioWriteBinding.inflate(layoutInflater)

        val coverImageHelper = CoverImageHelper(this)

        // 포트폴리오 수정인 경우 기존 데이터를 미리 설정
        portfolio?.let { p ->
            writeBinding.editTextPortfolioEditTitle.setText(p.title)
            writeBinding.editTextPortfolioEditContent.setText(p.content)
            writeBinding.editTextPortfolioEditGithub.setText(p.url)
            // 이미지가 있는 경우 CoverImageHelper 에서 이미지를 가져와서 설정
            portfolio.image?.let {
                writeBinding.imgPortfolioEditCover.setImageBitmap(
                    coverImageHelper.read(it)
                )
            }
        }

        // 이미지 이름을 임시로 저장하는 변수
        var imageFilename: String? = portfolio?.image

        // 사진 추가 버튼
        writeBinding.layoutPortfolioEditImage.setOnClickListener {
            // 사진이 이미 있는 경우 제거할 지 묻고, 없으면 사진을 가져옴
            if (imageFilename != null) {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(R.string.portfolio_write_image_remove)
                    .setMessage(R.string.portfolio_write_image_remove_message)
                    .setPositiveButton(R.string.remove) { d, _ ->
                        imageFilename = null
                        writeBinding.imgPortfolioEditCover.setImageBitmap(null)
                        d.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                    .show()
            } else {
                // 이미지 가져오면 ImageView에 가져온 이미지 표시
                onCoverImageResult = { filename ->
                    writeBinding.imgPortfolioEditCover.setImageBitmap(
                        coverImageHelper.read(filename)
                    )
                    imageFilename = filename
                }

                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                coverImageLauncher.launch(intent)
            }
        }

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(if (portfolio == null) R.string.portfolio_write_title else R.string.portfolio_edit_title)
            .setView(writeBinding.root)
            .setPositiveButton(R.string.confirm) { d, _ ->
                val title = writeBinding.editTextPortfolioEditTitle.text.toString().trim()
                val content = writeBinding.editTextPortfolioEditContent.text.toString().trim()
                val github = writeBinding.editTextPortfolioEditGithub.text.toString().trim()

                if (title.isEmpty()) {
                    makeSnackBar(R.string.portfolio_write_title_not_valid)
                    return@setPositiveButton
                }

                if (content.isEmpty()) {
                    makeSnackBar(R.string.portfolio_write_content_not_valid)
                    return@setPositiveButton
                }

                val regex = Regex(
                    """
                    ^((http|https)://(\w+:?\w*@)?(\S+)(:[0-9]+)?(/|/([\w#!:.?+=&%@\-/]))?)${'$'}
                    """.trimIndent()
                )
                if (github.isEmpty() && !github.matches(regex)) {
                    makeSnackBar(R.string.portfolio_write_url_not_valid)
                    return@setPositiveButton
                }

                val dao = PortfolioDAO(this)
                if (portfolio != null) {
                    portfolio.title = title
                    portfolio.content = content
                    portfolio.url = github
                    portfolio.image = imageFilename
                    dao.update(portfolio)
                    onFinish(portfolio)
                } else {
                    val p = Portfolio(0, member.id, title, content, github, imageFilename)
                    if (dao.insert(p)) {
                        dao.selectLast(member.id)
                        onFinish(p)
                    }
                }

                d.dismiss()
            }
            .setNeutralButton(R.string.cancel) { d, _ ->
                d.dismiss()
            }
            .show()
    }

    private var onCoverImageResult: ((String) -> Unit)? = null

    private val coverImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val uri = it.data?.data
            if (uri != null) {
                // Uri로부터 Bitmap 가져옴
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                // CoverImageHelper로 이미지 저장
                val helper = CoverImageHelper(this)
                val filename = helper.save(bitmap) ?: return@registerForActivityResult

                // 이미지 결과 리턴
                onCoverImageResult?.let {
                    it(filename)
                }
            }
        }

    private fun makeSnackBar(stringRes: Int) {
        Snackbar.make(binding.root, stringRes, Snackbar.LENGTH_LONG)
            .setAction(R.string.confirm) {}
            .show()
    }

    private fun makeToast(stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_OK)
    }
}