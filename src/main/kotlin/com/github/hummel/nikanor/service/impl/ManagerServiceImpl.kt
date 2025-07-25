package com.github.hummel.nikanor.service.impl

import com.github.hummel.nikanor.factory.ServiceFactory
import com.github.hummel.nikanor.service.AccessService
import com.github.hummel.nikanor.service.DataService
import com.github.hummel.nikanor.service.ManagerService
import com.github.hummel.nikanor.utils.I18n
import com.github.hummel.nikanor.utils.access
import com.github.hummel.nikanor.utils.error
import com.github.hummel.nikanor.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ManagerServiceImpl : ManagerService {
	private val dataService: DataService = ServiceFactory.dataService
	private val accessService: AccessService = ServiceFactory.accessService

	override fun addManagerRole(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_manager_role") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val roleId = arguments[0].toLong()
						guild.getRoleById(roleId) ?: throw Exception()

						guildData.managerRoleIds.add(roleId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("add_manager_role", guildData).format(roleId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearManagerRoles(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_managers") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("clear_manager_roles", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val roleId = arguments[0].toLong()

							guildData.managerRoleIds.removeIf { it == roleId }

							EmbedBuilder().success(
								event.member, guildData, I18n.of("clear_manager_roles_single", guildData).format(roleId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setLanguage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_language") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val lang = arguments[0]
						if (lang != "ru" && lang != "be" && lang != "uk" && lang != "en") {
							throw Exception()
						}

						guildData.lang = lang

						val langName = I18n.of(lang, guildData)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("set_language", guildData).format(langName)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setDiscordChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_discord_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val id = arguments[0].toLong()

						guildData.discordChannelId = id

						EmbedBuilder().success(
							event.member, guildData, I18n.of("set_discord_channel", guildData).format(id)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setTelegramChat(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_telegram_chat") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val id = arguments[0].toLong()

						guildData.telegramChatId = id

						EmbedBuilder().success(
							event.member, guildData, I18n.of("set_telegram_chat", guildData).format(id)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun commit(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "commit") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)
			val busRegistry = dataService.loadBusRegistry()

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				try {
					if (busRegistry.ownership.contains(guild.idLong)) {
						throw Exception()
					}

					if (guildData.discordChannelId == 0L || guildData.telegramChatId == 0L) {
						throw Exception()
					}

					val rule1 = !busRegistry.discordToTelegram.contains(guildData.discordChannelId)
					val rule2 = !busRegistry.telegramToDiscord.contains(guildData.telegramChatId)

					if (rule1 && rule2) {
						busRegistry.discordToTelegram.put(guildData.discordChannelId, guildData.telegramChatId)
						busRegistry.telegramToDiscord.put(guildData.telegramChatId, guildData.discordChannelId)
						busRegistry.ownership.put(
							guild.idLong, listOf(guildData.discordChannelId, guildData.telegramChatId)
						)
					} else {
						throw Exception()
					}

					EmbedBuilder().success(
						event.member, guildData, I18n.of("commit", guildData)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
				}
			}
			dataService.saveBusRegistry(busRegistry)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun uncommit(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "uncommit") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)
			val busRegistry = dataService.loadBusRegistry()

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				try {
					if (!busRegistry.ownership.contains(guild.idLong)) {
						throw Exception()
					}

					if (guildData.discordChannelId == 0L || guildData.telegramChatId == 0L) {
						throw Exception()
					}

					val rule1 = busRegistry.ownership[guild.idLong]!!.contains(guildData.discordChannelId)
					val rule2 = busRegistry.ownership[guild.idLong]!!.contains(guildData.telegramChatId)

					if (rule1 && rule2) {
						busRegistry.discordToTelegram.remove(guildData.discordChannelId)
						busRegistry.telegramToDiscord.remove(guildData.telegramChatId)
						busRegistry.ownership.remove(guild.idLong)
					} else {
						throw Exception()
					}

					EmbedBuilder().success(
						event.member, guildData, I18n.of("uncommit", guildData)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
				}
			}
			dataService.saveBusRegistry(busRegistry)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun wipeData(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "wipe_data") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				dataService.wipeGuildData(guild)

				EmbedBuilder().success(event.member, guildData, I18n.of("wipe_data", guildData))
			}
			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}